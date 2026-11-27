package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") String root)
    {
        this.root = Paths.get(root);
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(root);
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
	    Path path = this.resolvePath(id);
        try {
            Files.write(path, bytes);
            return id;
        } catch (IOException exception) {
            throw new UncheckedIOException("파일 저장 실패: " + path, exception);
        }
    }

    @Override
    // InputStream은 파일을 읽을 위치를 기억합니다
    public InputStream get(UUID id) {
        Path path = resolvePath(id);
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 열기 실패: " + path, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        // Resource resource = new InputStreamResource(this.get(dto.id()));

        // return ResponseEntity.ok(resource);

        // ContentDisposition.attachment()는
        // 브라우저에 파일을 첨부파일로 다운로드하라고 알려 주는 설정이야.
        // .filename(dto.fileName(), StandardCharsets.UTF_8)는 다운로드할 때
        // 제안할 파일명을 정해.
        // UTF-8을 지정하면 프로필.png처럼 한글이 들어간 이름도 헤더에 표현할 수 있어.
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(dto.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                // HttpHeaders.CONTENT_DISPOSITION은 헤더 이름인 "Content-Disposition"이고,
                // disposition.toString()은 앞에서 만든 첨부파일 설정과 파일명을 헤더에 넣을 문자열로 바꿔 줘.
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                // dto.contentType()이 "image/png"라는 문자열을 반환
                // parseMediaType(...)이 그 문자열을 스프링의 MediaType 객체로 바꿔
                // .contentType(...)은 이 값을 응답의 Content-Type 헤더에 설정해.
                .contentType(MediaType.parseMediaType(dto.contentType()))
                .contentLength(dto.size())
                .body(new InputStreamResource(get(dto.id())));
    }
    // 저장 폴더와 파일명(UUID)을 합친 경로를 반환
    public Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
