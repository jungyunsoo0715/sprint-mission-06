package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusListResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        /*
        if (!this.userRepository.existsById(request.userId())) {
            throw new UserNotFoundException(
                    "ID가 " + request.userId() + "인 사용자를 찾을 수 없습니다."
            );
        }
        if (!this.channelRepository.existsById(request.channelId())) {
            throw new ChannelNotFoundException(
                    "ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다."
            );
        }
         */

        User user = this.userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(
                        "ID가 " + request.userId() + "인 사용자를 찾을 수 없습니다."
                ));
        Channel channel = this.channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ChannelNotFoundException(
                        "ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다."
                ));

        if (this.readStatusRepository.existsByUserIdAndChannelId(
                request.userId(), request.channelId())) {
            throw new DuplicateResourceException(
                    "해당 사용자와 채널에 대한 메시지 수신 정보가 이미 존재합니다. 사용자 ID: "
                            + request.userId() + ", 채널 ID: " + request.channelId()
            );
        }

        ReadStatus readStatus = new ReadStatus(user, channel);

        readStatus = readStatusRepository.save(readStatus);

        return this.readStatusMapper.toReadStatusResponse(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = this.findReadStatusEntity(readStatusId);

        return this.readStatusMapper.toReadStatusResponse(readStatus);
    }

    private ReadStatus findReadStatusEntity(UUID readStatusId) {
        return this.readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(
                        "ID가 " + readStatusId + "인 메시지 수신 정보를 찾을 수 없습니다."
                ));
    }


    @Override
    public ReadStatusListResponse findAllByUserId(UUID userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
            );
        }

        List<ReadStatus> readStatuses = this.readStatusRepository.findAllByUserId(userId);

        List<ReadStatusResponse> readStatusResponses = readStatuses.stream()
                .map(this.readStatusMapper::toReadStatusResponse)
                .toList();

        return new ReadStatusListResponse(readStatusResponses);
    }

    @Override
    @Transactional
    public ReadStatusResponse update(UUID readStatusID, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = this.findReadStatusEntity(readStatusID);

        // readStatus.update();
        readStatus.updateLastReadAt(request.newLastReadAt());
        // this.readStatusRepository.save(readStatus);

        this.readStatusRepository.flush();

        return this.readStatusMapper.toReadStatusResponse(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!this.readStatusRepository.existsById(readStatusId)) {
            throw new ReadStatusNotFoundException(
                    "ID가 " + readStatusId + "인 메시지 수신 정보를 찾을 수 없습니다."
            );
        }

        this.readStatusRepository.deleteById(readStatusId);
    }
}
