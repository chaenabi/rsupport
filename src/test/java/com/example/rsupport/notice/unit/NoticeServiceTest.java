package com.example.rsupport.notice.unit;

import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import com.example.rsupport.api.notice.repository.NoticeAttachFileRepository;
import com.example.rsupport.api.notice.repository.NoticeRepository;
import com.example.rsupport.api.notice.service.NoticeService;
import com.example.rsupport.api.notice.utils.AttachFileManager;
import com.example.rsupport.exception.common.BizException;
import com.example.rsupport.exception.notice.NoticeCrudErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * 공지사항 서비스 로직 테스트
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@DisplayName("공지사항 서비스 테스트")
public class NoticeServiceTest {

    private final NoticeRepository noticeRepository = mock(NoticeRepository.class);
    private final NoticeAttachFileRepository noticeAttachFileRepository = mock(NoticeAttachFileRepository.class);
    private final AttachFileManager savingAttachFile = mock(AttachFileManager.class);
    private final NoticeService noticeService = new NoticeService(noticeRepository, noticeAttachFileRepository, savingAttachFile);

    @Nested
    @DisplayName("공지사항 등록 테스트")
    class NoticeRegisterTest {

        private final NoticeRegisterRequestDTO dto = NoticeRegisterRequestDTO
                .builder()
                .title("공지사항 제목")
                .content("공지사항 내용")
                .build();
        private final Notice wantToSaveNotice = Notice.builder()
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

        @BeforeEach
        void setUp() {
            String DEFAULT_UPLOAD_DIRECTORY = "src/main/resources/upload";
            LocalDateTime now = LocalDateTime.now();
            String randomString = UUID.randomUUID().toString().split("-")[0];

            attachFiles.forEach(file -> files.add(NoticeAttachFile.builder()
                    .filename(DEFAULT_UPLOAD_DIRECTORY
                            + "/"
                            + getBaseName(file.getOriginalFilename())
                            + "_"
                            + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                            + "_"
                            + randomString
                            + "."
                            + getExtension(file.getOriginalFilename())
                    )
                    .notice(wantToSaveNotice)
                    .build()));
        }

        @Test
        @DisplayName("공지사항 등록 성공")
        @SuppressWarnings("unchecked")
        void successRegisterNotice() {
            // 준비
            given(noticeRepository.save(any(Notice.class))).willReturn(wantToSaveNotice);
            given(savingAttachFile.saveUploadFilesToDisk(any(List.class), any(Notice.class))).willReturn(attachFiles);
            given(noticeAttachFileRepository.saveAll(any(List.class))).willReturn(files);

            Long noticeId = noticeService.registerNotice(dto, attachFiles);

            assertThat(wantToSaveNotice.getId()).isEqualTo(noticeId);
            files.clear();
        }

        @Test
        @DisplayName("공지사항 등록 실패_데이터베이스 트랜잭션 실패")
        @SuppressWarnings("unchecked")
        void failRegisterNotice_Database_Connection_Fail() {
            // 준비
            given(noticeRepository.save(any(Notice.class))).willThrow(new RuntimeException("database connection fail"));
            given(noticeAttachFileRepository.saveAll(any(List.class))).willThrow(new RuntimeException("database connection fail"));

            assertThatThrownBy(() -> noticeService.registerNotice(dto, attachFiles))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("database connection fail");
            files.clear();
        }

        @Test
        @DisplayName("공지사항 등록 실패_업로드된 첨부파일 저장 실패")
        @SuppressWarnings("unchecked")
        void failRegisterNotice_Saving_AttachFile_Fail() {
            // 준비
            given(noticeRepository.save(any(Notice.class))).willReturn(wantToSaveNotice);
            given(noticeAttachFileRepository.saveAll(any(List.class))).willReturn(files);

            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_CRUD_FAIL)).when(savingAttachFile).saveUploadFilesToDisk(any(List.class), refEq(wantToSaveNotice));

            assertThatThrownBy(() -> savingAttachFile.saveUploadFilesToDisk(attachFiles, wantToSaveNotice))
                    .isInstanceOf(BizException.class)
                    .hasMessage(NoticeCrudErrorCode.NOTICE_CRUD_FAIL.getMsg());
            files.clear();
        }
    }
}
