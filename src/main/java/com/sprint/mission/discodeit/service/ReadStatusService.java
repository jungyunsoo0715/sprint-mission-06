package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.*;

import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(ReadStatusCreateRequest request);

    ReadStatusDto find(UUID readStatusId);

    ReadStatusListResponse findAllByUserId(UUID userId);

    ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request);

    void delete(UUID readStatusId);
}
