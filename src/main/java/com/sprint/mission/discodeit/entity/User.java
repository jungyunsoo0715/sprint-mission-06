package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity // Entity는 클래스를 JPA의 영속성 관리 대상으로 지정해 DB 테이블과 매핑하도록 합니다.
@Table(name = "users")
@NoArgsConstructor // @Entity를 사용하면, 매개 변수가 없는 생성자가 반드시 있어야합니다.
// 이는 JPA 가 DB에서 값을 조회하여 필드에 채워넣기 때문에, 초기화 하는 객체의 필드 값이 필요없기 때문입니다.
public class User extends BaseUpdatableEntity {
    // 프로필 이미지는 변경 될 수 있기 때문에 프로필 이미지의 id(profileId)는 불변이여서는 안됩니다.
    // private UUID profileId;
    @OneToOne
    // profile_id는 User의 FK입니다. @JoinColumn은 외래 키가 사용합니다.
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;
    // UserStatus를 참조합니다.
    // private final UserStatus userStatus;
    @OneToOne(mappedBy = "user") // user_statuses 테이블에 user_id 외래키가 있습니다
    private UserStatus userStatus; // 필드가 불변이면 JPA가 DB에서 값을 조회 후 채워넣을 수 가 없습니다.
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;
    @Column(name = "password", nullable = false, length = 60)
    private String password;

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus
    }

    /*
    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.profileId = profileId;
        this.username = username;
        this.email = email;
        this.password = password;
    }
     */

    public User(BinaryContent profile, String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.profile = profile;
        // 양방향 참조는 의존성을 생성자로 주입하는 것 보다는
        // 세터로 주입 하는 것이 좋습니다.
        // this.userStatus = userStatus;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /*
    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
        }
    }
     */

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
