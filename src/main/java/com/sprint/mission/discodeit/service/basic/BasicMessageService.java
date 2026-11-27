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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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

    private final BinaryContentStorage binaryContentStorage;

    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageDto create(
            MessageCreateRequest request,
            List<MultipartFile> attachments) {

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

        List<byte[]> bytesList = attachments.stream()
                .map(a -> {
                    try {
                        return a.getBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<BinaryContent> binaryContents = attachments.stream()
                .map(a -> {
                    return new BinaryContent(
                            a.getOriginalFilename(),
                            a.getSize(),
                            a.getContentType()
                    );
                }).toList();

        binaryContents = this.binaryContentRepository.saveAll(binaryContents);

        for (int i = 0; i < binaryContents.size(); i++) {
            BinaryContent savedContent = binaryContents.get(i);
            this.binaryContentStorage.put(savedContent.getId(), bytesList.get(i));
        }

        return binaryContents;
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

        List<MessageDto> messageDtos =
                messageRepository.findAllByChannelId(channelId).stream()
                .map(this.messageMapper::toDto)
                .toList();

        return new MessageListResponse(messageDtos);
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

        message.update(newContent, newAttachments);

        this.messageRepository.flush();

        return this.messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = this.findMessageEntity(messageId);

        messageRepository.deleteById(messageId);
    }
}
