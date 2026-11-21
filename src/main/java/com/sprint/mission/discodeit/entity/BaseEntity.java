package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity {
    UUID id;

    @CreatedDate
    Instant createdAt;
}
