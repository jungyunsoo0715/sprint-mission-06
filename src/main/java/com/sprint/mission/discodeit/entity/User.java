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

    // User와 BinaryContent(Profile Image)는 1:1 연관 관계를 가집니다.
    // 하나의 사용자는 단 하나의 프로필 이미지를 가질 수 있습니다.
    @OneToOne
    // @JoinColumn은, 엔티티 사이의 연관 관계를, 어떤 외래 키로 연결할지 지정합니다.
    // @JoinColumn의 referencedColumnName이 생략되었으으로,
    // binary_contents 테이블의 id 컬럼이 기본으로 연결될 컬럼이 됩니다.
    // referencedColumnName 예시:
    // @JoinColumn(name = "profile_id", referencedColumnName = "id", unique = true)
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    // User와 UserStatus는 1:1 연관 관계를 가집니다.
    @OneToOne(
            // mappedBy는 상대 엔티티에서 이 연관관계의 외래 키를 관리하는 필드입니다.
            // 양방향 엔티티 참조에만 쓰입니다.
            mappedBy = "user",
            // JPA 작업이 참조하고 있는 엔티티에도 적용됩니다.
            cascade = CascadeType.ALL,
            // orphanRemoval = true 는
            // 부모와의 연결이 끊기면 그 자식 엔티티를 삭제해도 되는가를 기준으로 합니다.
            // FK로 참조하고 있는 엔티티(User)와의 연관관계가 끊어지면,
            // flush()시(userRepository.flush()와 같은 작업을 하는)에,
            // 해당 엔티티(UserStatus)가 삭제됩니다.
            orphanRemoval = true
            /*
            UserStatus는 User에 종속된 엔티티이기 때문에 그 두 엔티티 객체의 생명 주기를 함께
            관리할 필요성이 있습니다. 때문에 cascade로 User에 JPA 작업이 적용될 때,
            UserStatus도 JPA 작업(persist, merge, remove, refresh, detach)이 적용되게합니다.
            또한 User와 UserStatus의 관계가 끊어질 때, UserStatus는 더 이상 필요하지
            않으므로, orphanRemoval = true로 UserStatus를 자동으로 삭제하게 해줍니다.
             */
            /*
            persist — 새 엔티티를 영속성 컨텍스트에 등록합니다
	        merge — 전달한 엔티티의 상태를 영속성 컨텍스트가 관리하는 객체에 복사합니다.
            remove — 엔티티를 삭제 대상으로 지정합니다.
            refresh — DB에서 다시 읽은 값으로 엔티티 상태를 갱신합니다.
            detach — 엔티티를 영속성 컨텍스트의 관리에서 분리합니다.
             */
    )
    private UserStatus userStatus;

    @Column(
            name = "username",
            nullable = false, length = 50,
            unique = true
    )
    private String username;

    @Column(
            name = "email",
            nullable = false,
            length = 100,
            unique = true
    )
    private String email;

    @Column(
            name = "password",
            nullable = false,
            length = 60
    )
    private String password;

    public User(BinaryContent profile, String username, String email, String password) {
        this.profile = profile;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // @Setter가 클래스에 붙어있다해도, 정의된 메서드가 있다면 정의된 메서드만 사용됩니다.
    public void setUserStatus(UserStatus userStatus) {
        if (this.userStatus == userStatus) return;

        UserStatus previous = this.userStatus;
        this.userStatus = userStatus;

        // 전에 참조 했던 UserStatus가 존재하고,
        // 전에 참조 했던 UserStatus가 이 User를 참조한다면
        if (previous != null && previous.getUser() == this) {
            // 전에 참조 했던 UserStatus는 이 User를 더이상 참조하지않는다.
            // orphanRemoval = true 설정에 의해, 전에 참조 했던 UserStatus는 삭제됩니다.
            previous.setUser(null);
        }
        // 인자로 받은 userStatus가 존재하고
        // 이 UserStatus가 참조하는 User가 이 User가 아니라면
        // UserStatus가 이 User를 참조하도록한다.
        if (userStatus != null && userStatus.getUser() != this) {
            userStatus.setUser(this);
        }
    }

    public void update(String newUsername,
                       String newEmail,
                       String newPassword,
                       BinaryContent newProfile
    ) {
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
