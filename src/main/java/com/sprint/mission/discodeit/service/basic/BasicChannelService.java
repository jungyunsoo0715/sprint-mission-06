package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelUpdateRequestException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelModificationNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
        Channel publicChannel = new Channel(request);
        publicChannel = this.channelRepository.save(publicChannel);

        return this.channelMapper.toDto(publicChannel);
    }

    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel privateChannel = new Channel(request);
        Channel privateChannelPersisted = this.channelRepository.save(privateChannel);

        List<ReadStatus> readStatusList = request.participantIds().stream()
                .distinct()
                .map(userId -> {
                    User user = this.userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(
                                    "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
                            ));
                    return new ReadStatus(user, privateChannelPersisted);
                })
                .toList();

        this.readStatusRepository.saveAll(readStatusList);

        return this.channelMapper.toDto(privateChannelPersisted);
    }

    private Instant findLastMessageAt(UUID channelId) {
        return this.messageRepository
                .findFirstByChannelIdOrderByCreatedAtDesc(channelId)
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    private Set<UUID> findParticipantIds(UUID channelId) {
        return this.readStatusRepository.findAllByChannelId(channelId).stream()
                .map(r -> r.getUser().getId())
                .collect(Collectors.toSet());
    }

    private Channel findChannelEntity(UUID channelId) {
        return this.channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new ChannelNotFoundException(
                                "ID가 " + channelId + "인 채널을 찾을 수 없습니다."
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelListResponse findAllByUserId(UUID userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
            );
        }

        List<ReadStatus> readStatusesByUserId =
                this.readStatusRepository.findAllByUserId(userId);

        Set<UUID> channelIdsByUserId = readStatusesByUserId.stream()
                .map(r -> r.getChannel().getId())
                .collect(Collectors.toSet());

        List<ChannelResponse> privateChannelResponses =
                this.channelRepository.findAll().stream()
                        .filter(c -> c.getType() == ChannelType.PRIVATE
                        && channelIdsByUserId.contains(c.getId()))
                        .map(c -> {
                            Channel channel = c;
                            Set<UUID> participantIds = this.findParticipantIds(c.getId());
                            Instant lastMessageAt = this.findLastMessageAt(c.getId());

                            return this.channelMapper.toChannelResponse(
                                    channel,
                                    participantIds,
                                    lastMessageAt);
                        })
                        .distinct()
                        .collect(Collectors.toCollection(ArrayList::new));

        List<ChannelResponse> publicChannelResponses = this.channelRepository.findAll().stream()
                .filter(c -> c.getType() == ChannelType.PUBLIC)
                .distinct()
                .map((c) -> {
                    Channel channel = c;
                    Instant lastMessageAt = this.findLastMessageAt(c.getId());

                    return this.channelMapper.toChannelResponse(channel,
                            null,
                            lastMessageAt);
                })
                .distinct()
                .toList();

        privateChannelResponses.addAll(publicChannelResponses);

        return new ChannelListResponse(privateChannelResponses);
    }

    @Override
    @Transactional
    public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {
        if (request.newName() != null && request.newName().isBlank()) {
            throw new InvalidChannelUpdateRequestException(
                    "채널 이름은 공백으로만 구성할 수 없습니다."
            );
        }

        Channel channel = this.findChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelModificationNotAllowedException(
                    "프라이빗 채널은 수정할 수 없습니다."
            );
        }

        channel.update(request.newName(), request.newDescription());

        this.channelRepository.flush();

        return this.channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    public void delete(UUID channelId) {
        this.findChannelEntity(channelId);

        this.messageRepository.deleteAllByChannelId(channelId);

        this.channelRepository.deleteById(channelId);
    }
}
