package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
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
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.size(),
                request.contentType(),
                request.bytes());
        binaryContent = this.binaryContentRepository.save(binaryContent);

        return this.binaryContentMapper.toBinaryContentResponse(binaryContent);
    }


    @Override
    @Transactional(readOnly = true)
    public BinaryContentResponse find(UUID binaryContentId) {
        BinaryContent binaryContent =
                this.binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(
                        "ID가 " + binaryContentId + "인 파일을 찾을 수 없습니다."
                ));

        return this.binaryContentMapper.toBinaryContentResponse(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentListResponse findAllByIdIn(List<UUID> binaryContentIds) {

        List<BinaryContent> binaryContents =
                    this.binaryContentRepository.findAllById(binaryContentIds);

        List<BinaryContentResponse> binaryContentResponses = binaryContents.stream()
                .map(this.binaryContentMapper::toBinaryContentResponse)
                .toList();

        return new BinaryContentListResponse(binaryContentResponses);
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
