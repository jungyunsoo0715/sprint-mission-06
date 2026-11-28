package com.sprint.mission.discodeit.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {
    // 엔티티가 이미 영속성 컨텍스트의 관리 하에 있다면,
    // 엔티티의 필드가 변경되어, 변경 사항을 DB에 반영하기 직전에,
    // 이 애너테이션이 붙은 필드의 값을
    // 현재 시각으로 변경합니다.
    @LastModifiedDate
    Instant updatedAt;
}
