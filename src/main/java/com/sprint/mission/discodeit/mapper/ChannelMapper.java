package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                this.readStatusRepository.findAll().stream()
                        .map(r -> this.userMapper.toDto(r.getUser()))
                        .toList(),
                this.messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel)
                        .map(BaseEntity::getCreatedAt)
                        .orElse(null)
        );
    }

    public ChannelResponse toChannelResponse(Channel channel,
                                             Set<UUID> participantIds,
                                             Instant lastMessageAt) {
        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }

    /*
    public PrivateChannelResponseByUserId toPrivateChannelResponseByUserId(
            Channel channel,
            Set<UUID> participantIds,
            Instant lastMessageAt
    ) {
        return new PrivateChannelResponseByUserId(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }

    public PublicChannelResponse toPublicChannelResponse(
            Channel channel,
            Instant lastMessageAt
    ) {
        return new PublicChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt
        );
    }
     */
}
