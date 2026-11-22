package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusListResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    /*
    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (!this.userRepository.existsById(request.userId())) {
            throw new UserNotFoundException(
                    "ID가 " + request.userId() + "인 사용자를 찾을 수 없습니다."
            );
        }

        if (this.userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new DuplicateResourceException(
                    "해당 사용자의 사용자 상태 정보가 이미 존재합니다. 사용자 ID: "
                            + request.userId()
            );
        }

        UserStatus userStatus = new UserStatus(request.userId());

        this.userStatusRepository.save(userStatus);

        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }
     */

    @Override
    public UserStatusResponse find(UUID userStatusId) {
        UserStatus userStatus = this.userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                        "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다."
                ));

        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }

    @Override
    public UserStatusListResponse findAll() {
        List<UserStatus> userStatuses = this.userStatusRepository.findAll();

        List<UserStatusResponse> userStatusResponses = userStatuses.stream()
                .map(this.userStatusMapper::toUserStatusResponse)
                .toList();

        return new UserStatusListResponse(userStatusResponses);
    }

    @Override
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = this.userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                                "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다."
                        ));

        userStatus.update();
        this.userStatusRepository.save(userStatus);

        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }

    @Override
    @Transactional
    public UserStatusResponse updateByUserId(UUID userId) {

        User user = this.userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = user.getUserStatus();

        if (userStatus == null) {
            throw new UserStatusNotFoundException(
                    "사용자 ID " + user.getId() + "에 해당하는 UserStatus를 찾을 수 없습니다."
            );
        }

        userStatus.update();
        userStatus.updateOnlineStatus();

        this.userStatusRepository.flush();
        // this.userStatusRepository.save(userStatus);
        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!this.userStatusRepository.existsById(userStatusId)) {
            throw new UserStatusNotFoundException(
                    "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다.");
        }
        this.userStatusRepository.deleteById(userStatusId);
    }

}
