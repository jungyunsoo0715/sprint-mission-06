package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseUpdatableEntity {
    // private final UUID userId;
    private final User user;
    // private final UUID channelId;
    private final Channel channel;
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
