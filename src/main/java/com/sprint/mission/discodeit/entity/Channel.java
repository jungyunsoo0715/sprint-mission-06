package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "channels")
@NoArgsConstructor
@Getter
@Setter
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private ChannelType type;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    public Channel(PublicChannelCreateRequest request) {
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
