package com.sprint.mission.discodeit.oldrepository;

import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
    List<Message> findAllByChannelId();
}
