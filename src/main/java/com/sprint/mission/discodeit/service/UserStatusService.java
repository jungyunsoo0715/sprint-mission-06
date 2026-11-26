package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.*;

import java.util.UUID;

public interface UserStatusService {
    // UserStatusResponse create(UserStatusCreateRequest request);

    UserStatusDto find(UUID userStatusId);

    UserStatusListResponse findAll();

    UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

    UserStatusDto updateByUserId(UUID userId);

    void delete(UUID userStatusId);
}
