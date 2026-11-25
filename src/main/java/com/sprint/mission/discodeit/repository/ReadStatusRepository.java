package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID readStatusId);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    boolean existsById(UUID readStatusId);
    void deleteById(UUID id);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
    Optional<ReadStatus> findFirstByChannelIdOrderByLastReadAtDesc(UUID channelId);
}
