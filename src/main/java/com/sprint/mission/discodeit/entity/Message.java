package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "messages")
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Channel은 여러개의 Message를 가집니다.
    // Message는 하나의 Channel에만 소속됩니다.
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false) // messages.channel_id 외래 키와 매핑
    private Channel channel;

    // User는 여러개의 Message를 가집니다.
    // Message는 최대 하나의 User에만 소속됩니다.
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    // Message는 여러개의 Message Attachment를 가집니다.
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinTable(
            name = "message_attachments",
            // 내 쪽 Message의 ID를 저장할 컬럼
            joinColumns = @JoinColumn(name = "message_id"),
            // 상대쪽 BinaryContent의 ID를 저장할 컬럼
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments;

     public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
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
