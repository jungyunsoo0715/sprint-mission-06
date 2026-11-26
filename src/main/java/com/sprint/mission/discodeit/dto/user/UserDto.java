package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
//        UUID id,
//        Instant createdAt,
//        Instant updatedAt,
//        String username,
//        String email,
//        String password,
//        UUID profileId
        UUID id,
        String username,
        String email,
        BinaryContentDto profile,
        Boolean online
) {
}
