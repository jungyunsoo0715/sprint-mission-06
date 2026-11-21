package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "read_statuses")
@NoArgsConstructor
public class ReadStatus extends BaseUpdatableEntity {
    // private final UUID userId;
    private User user;
    // private final UUID channelId;
    private Channel channel;
    private Instant lastReadAt;

    /*
    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }
     */

    public ReadStatus(User user, Channel channel) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.channel = channel;
    }

    public void updateLastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; }
}
