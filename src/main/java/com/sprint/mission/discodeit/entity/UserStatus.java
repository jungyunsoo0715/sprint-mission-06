package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
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
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;
    @Transient
    private boolean isOnline;

    public void setUser(User user) {
        this.user = user;
    }

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
