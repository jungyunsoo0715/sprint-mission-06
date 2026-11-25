package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.authentication.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthMapper {
    public LoginResponse toLoginResponse(User user) {
        UUID profileId = null;

        if (user.getProfile() != null) {
            profileId = user.getProfile().getId();
        }

        return new LoginResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                profileId
        );
    }
}
