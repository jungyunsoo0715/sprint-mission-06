package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {
    @LastModifiedDate
    Instant updatedAt;
}
