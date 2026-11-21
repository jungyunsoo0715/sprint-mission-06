package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseUpdatableEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private Instant lastActiveAt;
    private boolean isOnline;

    public UserStatus(UUID userId) {
        this.id =  UUID.randomUUID();
        this.userId = userId;
        this.isOnline = true;
    }

    public void update() {
        this.lastActiveAt = this.updatedAt;
    }

    public void makeOnline() {
        Duration duration = Duration.between(this.updatedAt, Instant.now());
        this.isOnline = !duration.isNegative()
                && duration.compareTo(Duration.ofMinutes(5)) <= 0;
    }
}
