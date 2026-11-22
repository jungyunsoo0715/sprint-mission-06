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
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false) // messages.channel_id 외래 키와 매핑
    private Channel channel;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments;

     public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.attachments = attachments == null
                ? new ArrayList<>()
                : new ArrayList<>(attachments);
    }

    public void update(String newContent, List<BinaryContent> newAttachments) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
        if (newAttachments != null && !newAttachments.isEmpty()) {
            this.attachments.addAll(newAttachments);
        }
    }

}
