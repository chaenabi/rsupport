package com.example.rsupport.notice.unit;

import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import com.example.rsupport.api.notice.repository.NoticeAttachFileRepository;
import com.example.rsupport.api.notice.repository.NoticeRepository;
import com.example.rsupport.api.notice.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * 공지사항 서비스 로직 테스트
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
public class NoticeServiceTest {

    private NoticeRepository noticeRepository = mock(NoticeRepository.class);
    private NoticeAttachFileRepository noticeAttachFileRepository = mock(NoticeAttachFileRepository.class);
    private NoticeService noticeService = new NoticeService(noticeRepository, noticeAttachFileRepository);

    @Nested
    @DisplayName("공지사항 등록 테스트")
    class NoticeRegisterTest {

        private NoticeRegisterRequestDTO dto = NoticeRegisterRequestDTO
                .builder()
                .title("공지사항 제목")
                .content("공지사항 내용")
                .build();
        private Notice wantTosaveNotice = Notice.builder()
                .id(1L)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        File file1 = new File("src/test/resources/avatar.jpg");
        File file2 = new File("src/test/resources/notice.txt");
        MultipartFile attachFile1 = new MockMultipartFile("attachFiles", file1.getName(), MULTIPART_FORM_DATA_VALUE, file1.getName().getBytes());
        MultipartFile attachFile2 = new MockMultipartFile("attachFiles", file2.getName(), MULTIPART_FORM_DATA_VALUE, file2.getName().getBytes());
        List<MultipartFile> attachFiles = List.of(attachFile1, attachFile2);
        List<NoticeAttachFile> files = new ArrayList<>();

        @Test
        @DisplayName("공지사항 등록 성공")
        void successRegisterNotice() {
            // 준비
            attachFiles.forEach(file -> files.add(NoticeAttachFile.builder()
                    .filePath(file.getOriginalFilename())
                    .notice(wantTosaveNotice)
                    .build()));

            given(noticeRepository.save(any(Notice.class))).willReturn(wantTosaveNotice);
            given(noticeAttachFileRepository.saveAll(any(List.class))).willReturn(files);

            Long noticeId = noticeService.registerNotice(dto, attachFiles);

            assertThat(wantTosaveNotice.getId()).isEqualTo(noticeId);
            files.clear();
        }

        @Test
        @DisplayName("공지사항 등록 실패")
        void failRegisterNotice_Database_Connection_Fail() {
            // 준비
            attachFiles.forEach(file -> files.add(NoticeAttachFile.builder()
                    .filePath(file.getOriginalFilename())
                    .notice(wantTosaveNotice)
                    .build()));

            given(noticeRepository.save(any(Notice.class))).willThrow(new RuntimeException("database connection fail"));
            given(noticeAttachFileRepository.saveAll(any(List.class))).willThrow(new RuntimeException("database connection fail"));

            assertThatThrownBy(() -> noticeService.registerNotice(dto, attachFiles))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("database connection fail");
            files.clear();
        }
    }
}
