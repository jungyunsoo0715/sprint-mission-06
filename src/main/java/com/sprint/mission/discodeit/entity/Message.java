package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor
public class Message extends BaseUpdatableEntity {
    private String content;
    // private final UUID channelId;
    private Channel channel;
    // private final UUID authorId;
    private User author;
    // private final List<UUID> attachmentIds;
    private List<BinaryContent> attachments;

    /*
    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds == null
                ? new ArrayList<>()
                : new ArrayList<>(attachmentIds);
    }
     */

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.attachments = attachments == null
                ? new ArrayList<>()
                : new ArrayList<>(attachments);
    }

    /*
    public void update(String newContent, List<UUID> newAttachmentIds) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
        if (!newAttachmentIds.isEmpty()) {
            this.attachmentIds.addAll(newAttachmentIds);
        }
    }
     */

    public void update(String newContent, List<BinaryContent> newAttachments) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
        if (!newAttachments.isEmpty()) {
            this.attachments.addAll(newAttachments);
        }
    }

}
