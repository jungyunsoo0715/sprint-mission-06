package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.time.Instant;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {
    Instant updatedAt;
}
