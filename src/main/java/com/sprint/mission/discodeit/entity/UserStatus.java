package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor
public class UserStatus extends BaseUpdatableEntity {
    // private final UUID userId;
    private User user;
    private Instant lastActiveAt;
    private boolean isOnline;

    /*
    public UserStatus(UUID userId) {
        this.id =  UUID.randomUUID();
        this.userId = userId;
        this.isOnline = true;
        this.lastActiveAt = Instant.now();
    }
     */

    public UserStatus(User user) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.isOnline = true;
        this.lastActiveAt = Instant.now();
    }

    public void update() {
        this.lastActiveAt = Instant.now();
    }

    public void makeOnline() {
        Duration duration = Duration.between(this.lastActiveAt, Instant.now());
        this.isOnline = !duration.isNegative()
                && duration.compareTo(Duration.ofMinutes(5)) <= 0;
    }
}
