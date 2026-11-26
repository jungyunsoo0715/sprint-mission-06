package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserStatusMapper {
    public UserStatusDto toDto(UserStatus userStatus) {
        UUID userId = null;
        if (userStatus.getUser() != null) {
            userId = userStatus.getUser().getId();
        }
        return new UserStatusDto(
                userStatus.getId(),
                userId,
                userStatus.getLastActiveAt()
        );
    }

    public UserStatusResponse toUserStatusResponse(UserStatus userStatus) {
        UUID userId = null;

        if (userStatus.getUser() != null) {
            userId = userStatus.getUser().getId();
        }

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userId,
                userStatus.getLastActiveAt(),
                userStatus.isOnline() // 필드명이 is로 시작하면 get을 붙이지 않습니다.
        );
    }
}
