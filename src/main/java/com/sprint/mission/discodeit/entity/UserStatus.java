package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity(name = "user_statuses")
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {
    // UserStatus와 User는 1:1 연관관계를 가집니다.
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    @Transient
    private boolean isOnline;

    public UserStatus(User user) {
        this.user = user;
        this.isOnline = true;
        this.lastActiveAt = Instant.now();
    }

    public void setUser(User user) {
        if (this.user == user) return;

        User previous = this.user;
        this.user = user;

        if (previous != null && previous.getUserStatus() == this) {
            previous.setUserStatus(null);
        }
        if (user != null && user.getUserStatus() != this) {
            user.setUserStatus(this);
        }
    }

    public void update() {
        this.lastActiveAt = Instant.now();
    }

    public void updateOnlineStatus() {
        Duration duration = Duration.between(this.lastActiveAt, Instant.now());
        this.isOnline = !duration.isNegative()
                && duration.compareTo(Duration.ofMinutes(5)) <= 0;
    }
}
