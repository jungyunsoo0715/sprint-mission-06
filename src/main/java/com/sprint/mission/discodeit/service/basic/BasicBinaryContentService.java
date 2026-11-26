package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentListResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentMapper binaryContentMapper;

    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.size(),
                request.contentType(),
                request.bytes());
        binaryContent = this.binaryContentRepository.save(binaryContent);

        return this.binaryContentMapper.toDto(binaryContent);
    }


    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent =
                this.binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(
                        "ID가 " + binaryContentId + "인 파일을 찾을 수 없습니다."
                ));

        return this.binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentListResponse findAllByIdIn(List<UUID> binaryContentIds) {

        List<BinaryContent> binaryContents =
                    this.binaryContentRepository.findAllById(binaryContentIds);

        List<BinaryContentDto> binaryContentDtos = binaryContents.stream()
                .map(this.binaryContentMapper::toDto)
                .toList();

        return new BinaryContentListResponse(binaryContentDtos);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!this.binaryContentRepository.existsById(id)) {
            throw new BinaryContentNotFoundException(
                    "ID가 " + id + "인 파일을 찾을 수 없습니다."
            );
        }

        this.binaryContentRepository.deleteById(id);
    }
}
