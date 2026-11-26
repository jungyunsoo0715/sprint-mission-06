package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments);

    MessageDto find(UUID messageId);

    MessageListResponse findAllByChannelId(UUID channelId);

    MessageDto update(UUID messageId, MessageUpdateRequest request, List<MultipartFile> attachments);

    void delete(UUID messageId);
}
