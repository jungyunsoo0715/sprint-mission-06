package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity // Entity는 클래스를 JPA의 영속성 관리 대상으로 지정해 DB 테이블과 매핑하도록 합니다.
@NoArgsConstructor // @Entity를 사용하면, 매개 변수가 없는 생성자가 반드시 있어야합니다.
// 이는 JPA 가 DB에서 값을 조회하여 필드에 채워넣기 때문에, 초기화 하는 객체의 필드 값이 필요없기 때문입니다.
public class User extends BaseUpdatableEntity {
    // 프로필 이미지는 변경 될 수 있기 때문에 프로필 이미지의 id(profileId)는 불변이여서는 안됩니다.
    // private UUID profileId;
    private BinaryContent profile;
    // UserStatus를 참조합니다.
    // private final UserStatus userStatus;
    private UserStatus userStatus;
    private String username;
    private String email;
    private String password;

    /*
    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.profileId = profileId;
        this.username = username;
        this.email = email;
        this.password = password;
    }
     */

    public User(BinaryContent profile, UserStatus userStatus, String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.profile = profile;
        this.userStatus = userStatus;
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
