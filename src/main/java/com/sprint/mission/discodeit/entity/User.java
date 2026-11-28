package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity(name = "users") // @Table을 생략하면, 이 이름이 기본 테이블 명으로 사용됩니다.
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users") // 둘 다 사용하면, @Entity(name = ...) 은 JPQL에서 사용 할 이름이 됩니다.
public class User extends BaseUpdatableEntity {

    @OneToOne
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    @Setter
    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserStatus userStatus;
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;
    @Column(name = "password", nullable = false, length = 60)
    private String password;

    public User(BinaryContent profile, String username, String email, String password) {
        this.profile = profile;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
    }
}
