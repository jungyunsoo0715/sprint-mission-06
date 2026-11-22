package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor
public class Message extends BaseUpdatableEntity {
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    // private final UUID channelId;
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false) // messages.channel_id 외래 키와 매핑
    private Channel channel;
    // private final UUID authorId;
    @ManyToOne
    @JoinColumn(name = "author_id") // messages.author_id 외래 키와 매핑
    private User author;
    // private final List<UUID> attachmentIds;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinTable(
            // message_attachments 테이블을 통해 연결
            name = "message_attachments",
            // Message의 ID를 참조하는 message_id
            joinColumns = @JoinColumn(name = "message_id"),
            // BinaryContent의 ID를 참조하는 attachment_id
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
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
        if (newAttachments != null && !newAttachments.isEmpty()) {
            this.attachments.addAll(newAttachments);
        }
    }

}
