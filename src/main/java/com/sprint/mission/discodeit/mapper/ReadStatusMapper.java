package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReadStatusMapper {
    public ReadStatusResponse toReadStatusResponse(ReadStatus readStatus) {
        UUID userId = null;
        UUID channelId = null;

        if (readStatus.getUser() != null) {
             userId = readStatus.getUser().getId();
        }
        if (readStatus.getChannel() != null) {
            channelId = readStatus.getChannel().getId();
        }

        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                userId,
                channelId,
                readStatus.getLastReadAt()
        );
    }
}
