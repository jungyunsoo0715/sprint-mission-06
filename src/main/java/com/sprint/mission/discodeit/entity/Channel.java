package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {
    private ChannelType type;
    private String name;
    private String description;

    public Channel(PublicChannelCreateRequest request) {
        this.id = UUID.randomUUID();
        this.type = ChannelType.PUBLIC;
        this.name = request.name();
        this.description = request.description();
    }

    public Channel(PrivateChannelCreateRequest request) {
        this.id = UUID.randomUUID();
        this.type = ChannelType.PRIVATE;
        this.name = null;
        this.description = null;
    }

    public void update(String newName, String newDescription) {
        if (newName != null && !newName.isBlank() && !newName.equals(this.name)) {
            this.name = newName;
        }

        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
    }
}
