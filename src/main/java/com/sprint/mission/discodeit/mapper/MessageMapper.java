package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MessageMapper {
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
