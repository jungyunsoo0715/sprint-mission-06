package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity(name = "read_statuses")
@NoArgsConstructor
@Getter
@Setter
@Table(name = "read_statuses")
public class ReadStatus extends BaseUpdatableEntity {
    // User는 여러개의 ReadStatus를 가지고, ReadStatus는 반드시 단 하나의 User에 속합니다.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Channel은 여러개의 ReadStatus를 가지고, ReadStatus는 반드시 단 하나의 Channel에 속합니다.
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    /*
    User와 ReadStatus,
    Channel과 ReadStatus는 생성과 수정이 서로에게 독립적이기 때문에
    cascade를 사용하지 않습니다.
    @ManyToOne에는 orphanRemoval 옵션 자체가 없기 때문에 이를 사용할 수 없습니다.
    orphanRemoval이 쓰이는 곳은 부모 엔티티의 자식 필드입니다.

    만약 @ManyToOne에 orphanRemoval 옵션이 있다고 가정하면 Many쪽이 부모, One쪽이 자식이 됩니다.
    여러 부모 엔티티중 하나의 부모엔티티와 자식의 연관 관계가 끊어졌다고 해도, 여전히 다른 부모 엔티티드과
    연관 관계가 유지되고있기 때문에, 자식 엔티티를 삭제할 이유가 없습니다. 때문에 @ManyToOne에는
    orphanRemoval 옵션이 존재하지 않습니다.
     */

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
    }

    // ReadStatus는 User와 Channel을 한쪽에서만 참조하기때문에
    // 양쪽 참조를 맞추는 메서드가 필요하지 않습니다.

    public void updateLastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; }
}
