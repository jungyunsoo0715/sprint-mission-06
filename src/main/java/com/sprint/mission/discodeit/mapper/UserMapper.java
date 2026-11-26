package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        BinaryContentDto profile = null;

        if (user.getProfile() != null) {
            profile = this.binaryContentMapper.toDto(user.getProfile());
        }

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile,
                user.getUserStatus().isOnline()
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
