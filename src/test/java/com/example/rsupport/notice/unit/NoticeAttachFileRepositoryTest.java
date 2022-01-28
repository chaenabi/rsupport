package com.example.rsupport.notice.unit;

import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import com.example.rsupport.api.notice.repository.NoticeAttachFileRepository;
import com.example.rsupport.api.notice.repository.NoticeAttachFileRepositoryImpl;
import com.example.rsupport.api.notice.repository.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * 공지사항 정보와 관련된 데이터베이스 CRUD 단위테스트
 * <p>
 * h2는 임베디드 DB를 사용하므로, inmemory 설정을 사용할 수 있도록
 * # @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
 * 를 설정합니다.
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("공지사항 첨부파일 레포지토리 테스트")
public class NoticeAttachFileRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeAttachFileRepository noticeAttachFileRepository;

    @Autowired
    private NoticeAttachFileRepositoryImpl noticeAttachFileQueryDSLRepository;

    @Test
    @DisplayName("특정 공지사항과 관련된 첨부파일 목록을 읽을 수 있어야 합니다")
    @Rollback(value = true)
    void successFindByNoticeId() {
        // 준비
        File file1 = new File("src/test/resources/avatar.jpg");
        File file2 = new File("src/test/resources/notice.txt");
        MultipartFile attachFile1 = new MockMultipartFile("attachFiles", file1.getName(), MULTIPART_FORM_DATA_VALUE, file1.getName().getBytes());
        MultipartFile attachFile2 = new MockMultipartFile("attachFiles", file2.getName(), MULTIPART_FORM_DATA_VALUE, file2.getName().getBytes());

        List<MultipartFile> attachFiles = List.of(attachFile1, attachFile2);
        final List<NoticeAttachFile> list = new ArrayList<>();
        Notice notice = Notice.builder()
                .title("공지사항 제목")
                .content("공지사항 내용")
                .build();

        Notice save = noticeRepository.save(notice);

        attachFiles.forEach(file -> {
            NoticeAttachFile noticeAttachFile = NoticeAttachFile.builder()
                    .filename(file.getOriginalFilename())
                    .notice(save)
                    .build();
            list.add(noticeAttachFile);
        });

        noticeAttachFileRepository.saveAll(list);

        // 실행
        List<NoticeAttachFile> byNoticeId = noticeAttachFileQueryDSLRepository.findByNoticeId(save.getId());

        // 검증
        assertThat(byNoticeId)
                .extracting("filename").containsExactlyInAnyOrder(
                        Objects.requireNonNull(attachFile1.getOriginalFilename()),
                        Objects.requireNonNull(attachFile2.getOriginalFilename())
                );
    }

}
