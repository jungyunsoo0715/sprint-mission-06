package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageDto create(
            MessageCreateRequest request,
            List<MultipartFile> attachments) {
        /*
        if (!channelRepository.existsById(request.channelId())) {
            throw new ChannelNotFoundException(
                    "ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다."
            );
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new UserNotFoundException(
                    "ID가 " + request.authorId() + "인 사용자를 찾을 수 없습니다."
            );
        }
         */

        Channel channel = this.channelRepository.findById(request.channelId())
                .orElseThrow(() ->
                        new ChannelNotFoundException(
                                "ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다."));

        User author = this.userRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "ID가 " + request.authorId() + "인 사용자를 찾을 수 없습니다."
                        ));

        List<BinaryContent> newAttachments = null;

        newAttachments = createBinaryContents(attachments);

        /*
        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                newAttachmentIds);
         */

        Message message = new Message(
                request.content(),
                channel,
                author,
                newAttachments
        );

        message = messageRepository.save(message);

        return this.messageMapper.toDto(message);
    }

    private List<BinaryContent> createBinaryContents (
            List<MultipartFile> attachments
    ) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> binaryContents = attachments.stream()
                .map(a -> {
                    try {
                        return new BinaryContent(
                                a.getOriginalFilename(),
                                a.getSize(),
                                a.getContentType(),
                                a.getBytes()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        // binaryContents.forEach(this.binaryContentRepository::save);

        // return binaryContents;

        return this.binaryContentRepository.saveAll(binaryContents);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        Message message = this.findMessageEntity(messageId);

        return this.messageMapper.toDto(message);
    }

    private Message findMessageEntity(UUID messageId) {
        return this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "ID가 " + messageId + "인 메시지를 찾을 수 없습니다."
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public MessageListResponse findAllByChannelId(UUID channelId) {

        if (!this.channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(
                    "ID가 " + channelId + "인 채널을 찾을 수 없습니다."
            );
        }

        /*
        List<MessageResponse> messageResponses = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this.messageMapper::toMessageResponse)
                .toList();
         */

        List<MessageResponse> messageResponses =
                messageRepository.findAllByChannelId(channelId).stream()
                .map(this.messageMapper::toMessageResponse)
                .toList();

        return new MessageListResponse(messageResponses);
    }

    @Override
    @Transactional
    public MessageDto update(
            UUID messageId,
            MessageUpdateRequest request,
            List<MultipartFile> attachments) {
        Message message = this.findMessageEntity(messageId);
        String newContent;

        if (request.newContent() != null &&
                !request.newContent().isBlank()) {
            newContent = request.newContent();
        } else {
            newContent = message.getContent();
        }

        List<BinaryContent> newAttachments= createBinaryContents(attachments);
        /*
        List<UUID> newAttachmentIds = binaryContents.stream()
                .map(BinaryContent::getId)
                .toList();
         */

        message.update(newContent, newAttachments);
        // this.messageRepository.save(message);

        this.messageRepository.flush();

        return this.messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = this.findMessageEntity(messageId);
        // Message.attachments 의 설정에 메시지를 삭제하면 첨부 파일을 자동으로 같이
        // 삭제하는 설정이 존재합니다. 때문에 첨부 파일엔티티를 같이 삭제할 필요가 없습니다.
        /*
        List<UUID> attachmentIds = message.getAttachments().stream()
                .map(BaseEntity::getId)
                .toList();
        attachmentIds.forEach(this.binaryContentRepository::deleteById);
         */
        messageRepository.deleteById(messageId);
    }
}
