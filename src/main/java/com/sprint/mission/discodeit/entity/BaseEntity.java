package com.sprint.mission.discodeit.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
// 부모 클래스의 필드를 자식 엔티티의 필드와 함께 DB 테이블에 매핑하도록 설정합니다.
@MappedSuperclass
// JPA Auditing 리스너를 등록합니다. 리스너는 엔티티 생성·수정 이벤트에 반응해
// @CreatedDate, @LastModifiedDate가 붙은 필드의 값을 자동으로 설정합니다.
// 자식 엔티티에도 상속되므로 최상위 클래스에만 선언합니다.
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    UUID id;

    @CreatedDate
    Instant createdAt;
}
