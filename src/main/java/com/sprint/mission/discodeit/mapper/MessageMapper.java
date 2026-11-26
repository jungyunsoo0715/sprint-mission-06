package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        UUID channelId = null;
        UserDto author = null;
        List<BinaryContentDto> attachments = new ArrayList<>();

        if (message.getChannel() != null) {
            channelId = message.getChannel().getId();
        }
        if (message.getAuthor() != null) {
            author = this.userMapper.toDto(message.getAuthor());
        }
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            attachments = message.getAttachments().stream()
                    .map(this.binaryContentMapper::toDto)
                    .toList();
        }

        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                channelId,
                author,
                attachments
        );
    }

    public MessageResponse toMessageResponse(Message message) {
        UUID channelId = null;
        UUID authorId = null;
        List<UUID> attachmentIds = new ArrayList<>();

        if (message.getChannel() != null) {
            channelId = message.getChannel().getId();
        }
        if (message.getAuthor() != null) {
            authorId = message.getAuthor().getId();
        }
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            attachmentIds = message.getAttachments().stream()
                    .map(BaseEntity::getId)
                    .toList();
        }

        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                channelId,
                authorId,
                attachmentIds
        );
    }
}
