package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        UUID profileId = null;

        if (user.getProfile() != null) {
            profileId = user.getProfile().getId();
        }

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                profileId
        );
    }

    public UserResponse toUserResponse(User user, UserStatus userStatus) {
        UUID profileId = null;

        if (user.getProfile() != null) {
            profileId = user.getProfile().getId();
        }

        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                userStatus.isOnline(),
                user.getEmail(),
                profileId
        );
    }
}
