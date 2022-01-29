package com.example.rsupport.notice.unit;

import com.example.rsupport.api.notice.domain.dto.NoticeDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeUpdateRequestDTO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Nested
    @DisplayName("공지사항 수정 테스트")
    class NoticeUpdateTest {

        private final Long wantToUpdateNoticeId = 1L;

        private final NoticeUpdateRequestDTO dto = NoticeUpdateRequestDTO
                .builder()
                .noticeId(wantToUpdateNoticeId)
                .title("수정된 공지사항 제목")
                .content("수정된 공지사항 내용")
                .build();

        private final Notice wantToUpdateNotice = Notice.builder()
                .id(wantToUpdateNoticeId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

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
                    .notice(wantToUpdateNotice)
                    .build()));
        }

        File file1 = new File("src/test/resources/avatar.jpg");
        File file2 = new File("src/test/resources/notice.txt");
        MultipartFile attachFile1 = new MockMultipartFile("attachFiles", file1.getName(), MULTIPART_FORM_DATA_VALUE, file1.getName().getBytes());
        MultipartFile attachFile2 = new MockMultipartFile("attachFiles", file2.getName(), MULTIPART_FORM_DATA_VALUE, file2.getName().getBytes());
        List<MultipartFile> attachFiles = List.of(attachFile1, attachFile2);
        List<NoticeAttachFile> files = new ArrayList<>();

        @Test
        @DisplayName("공지사항 수정 성공")
        @SuppressWarnings("all")
        void successUpdateNotice() {

            // 준비
            given(noticeRepository.findById(wantToUpdateNoticeId)).willReturn(Optional.of(wantToUpdateNotice));
            doNothing().when(noticeAttachFileRepository).deleteAll(anyList());
            doNothing().when(noticeAttachFileRepository).flush();
            given(noticeAttachFileRepository.saveAll(anyList())).willReturn(files);

            // 실행
            Long noticeId = noticeService.updateNotice(dto, attachFiles);

            // 검증
            verify(noticeRepository, atMostOnce()).findById(refEq(wantToUpdateNoticeId));
            verify(noticeAttachFileRepository, atMostOnce()).deleteAll(files);
            verify(noticeAttachFileRepository, atMostOnce()).flush();
            verify(noticeAttachFileRepository, atMostOnce()).saveAll(files);
            assertThat(noticeId).isEqualTo(wantToUpdateNoticeId);
            files.clear();
        }

        @Test
        @DisplayName("공지사항 수정 실패_공지사항이 존재하지 않음")
        void failUpdateNotice_No_Exist_Notice() {
            // 준비
            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND)).when(noticeRepository).findById(wantToUpdateNoticeId);

            // 실행 및 검증
            assertThatThrownBy(() -> noticeRepository.findById(wantToUpdateNoticeId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(NoticeCrudErrorCode.NOTICE_NOT_FOUND.getMsg());
            files.clear();
        }

        @Test
        @DisplayName("공지사항 수정 실패_첨부파일 저장 실패")
        @SuppressWarnings("unchecked")
        void failUpdateNotice_Save_AttachFile_Fail() {
            // 준비
            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_CRUD_FAIL)).when(savingAttachFile).saveUploadFilesToDisk(any(List.class), refEq(wantToUpdateNotice));

            // 실행 및 검증
            assertThatThrownBy(() -> savingAttachFile.saveUploadFilesToDisk(attachFiles, wantToUpdateNotice))
                    .isInstanceOf(BizException.class)
                    .hasMessage(NoticeCrudErrorCode.NOTICE_CRUD_FAIL.getMsg());
            files.clear();
        }
    }

    @Nested
    @DisplayName("공지사항 삭제 테스트")
    class NoticeDeleteTest {

        private final Long wantToDeleteNoticeId = 1L;

        @Test
        @DisplayName("공지사항 삭제 성공")
        void successDeleteNotice() {
            // 준비
            Notice findNoticeToDelete = Notice.builder()
                    .id(wantToDeleteNoticeId)
                    .title("삭제될 공지사항의 제목")
                    .content("삭제될 공지사항의 내용")
                    .build();

            given(noticeRepository.findById(1L)).willReturn(Optional.of(findNoticeToDelete));
            doNothing().when(noticeRepository).deleteById(wantToDeleteNoticeId);

            // 실행
            verify(noticeRepository, atMostOnce()).deleteById(wantToDeleteNoticeId);
        }

        @Test
        @DisplayName("공지사항 삭제 실패")
        void failDeleteNotice() {
            // 준비
            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND))
                    .when(noticeRepository)
                    .deleteById(wantToDeleteNoticeId);

            // 실행 및 검증
            assertThatThrownBy(() -> noticeRepository.deleteById(wantToDeleteNoticeId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(NoticeCrudErrorCode.NOTICE_NOT_FOUND.getMsg());
        }
    }

    @Nested
    @DisplayName("공지사항 단건 조회 테스트")
    class NoticeSelectOneTest {

        private final Long wantToSelectNoticeId = 1L;

        @Test
        @DisplayName("공지사항 단건 조회 성공")
        void successSelectOneNotice() {
            // 준비
            Notice findOneNotice = Notice.builder()
                    .id(wantToSelectNoticeId)
                    .title("조회될 공지사항의 제목")
                    .content("조회될 공지사항의 내용")
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(30))
                    .sawCount(10)
                    .build();

            given(noticeRepository.findById(1L)).willReturn(Optional.of(findOneNotice));

            NoticeDTO result = NoticeDTO.builder()
                    .noticeId(findOneNotice.getId())
                    .title(findOneNotice.getTitle())
                    .content(findOneNotice.getContent())
                    .startTime(findOneNotice.getStartTime())
                    .endTime(findOneNotice.getEndTime())
                    .sawCount(findOneNotice.getSawCount())
                    .build();

            // 실행
            NoticeDTO noticeDTO = noticeService.selectNoticeOne(wantToSelectNoticeId);

            assertThat(noticeDTO).extracting("noticeId").isEqualTo(result.getNoticeId());
            assertThat(noticeDTO).extracting("title").isEqualTo(result.getTitle());
            assertThat(noticeDTO).extracting("content").isEqualTo(result.getContent());
            assertThat(noticeDTO).extracting("startTime").isEqualTo(result.getStartTime());
            assertThat(noticeDTO).extracting("endTime").isEqualTo(result.getEndTime());
            assertThat(noticeDTO).extracting("sawCount").isEqualTo(result.getSawCount() + 1);
        }

        @Test
        @DisplayName("공지사항 단건 조회 실패")
        void failSelectOneNotice() {
            // 준비
            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND))
                    .when(noticeRepository)
                    .findById(wantToSelectNoticeId);

            // 실행 및 검증
            assertThatThrownBy(() -> noticeRepository.findById(wantToSelectNoticeId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(NoticeCrudErrorCode.NOTICE_NOT_FOUND.getMsg());
        }
    }
}
