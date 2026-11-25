package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    // 사용하지 않는 파생 쿼리 메서드라도,
    // Spring은 애플리케이션 시작 시 쿼리 생성 및 검증을 수행하기 때문에 오류가 발생합니다.
    // List<Channel> findAllByUserId(UUID userId);
}
