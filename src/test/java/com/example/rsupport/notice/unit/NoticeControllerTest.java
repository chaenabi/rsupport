package com.example.rsupport.notice.unit;

import com.example.rsupport.api.notice.controller.NoticeController;
import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeUpdateRequestDTO;
import com.example.rsupport.api.notice.domain.enums.NoticeMessage;
import com.example.rsupport.api.notice.service.NoticeService;
import com.example.rsupport.exception.common.BizException;
import com.example.rsupport.exception.notice.NoticeCrudErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 공지사항 컨트롤러 테스트
 * <p>
 * 클라이언트로부터 전달받는 공지사항에 대한 모든 매개변수는
 * 공지사항 컨트롤러에서 모두 검증됩니다.
 * 따라서 서비스 객체부터는 추가 검증이 요구되지 않습니다.
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@WebMvcTest(controllers = NoticeController.class)
public class NoticeControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private NoticeService noticeService;

    @Nested
    class NoticeRegisterTest {

        private NoticeRegisterRequestDTO registerRequestDTO;

        @Test
        @DisplayName("공지사항 등록이 성공적으로 수행되어야 합니다")
        void successRegister() throws Exception {
            // 준비
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("공지사항 제목")
                    .content("공지사항 내용")
                    .build();

            given(noticeService.registerNotice(any(NoticeRegisterRequestDTO.class), any(List.class))).willReturn(1L);
            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    "application/json",
                    mapper.writeValueAsString(registerRequestDTO).getBytes());

            // 실행
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .characterEncoding("UTF-8")
                    .header("Content-Type", MediaType.MULTIPART_FORM_DATA)
            );

            // 검증
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1L))
                    .andExpect(jsonPath("$.message").value(NoticeMessage.SUCCESS_NOTICE_REGISTER.getSuccessMsg()));

            verify(noticeService, times(1)).registerNotice(refEq(registerRequestDTO), any(List.class));
        }

        @Test
        @DisplayName("공지사항 등록이 성공적으로 수행되어야 합니다_다중 첨부파일 포함")
        void successRegisterIncludeAttachFiles() throws Exception {
            // 준비
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("공지사항 제목")
                    .content("공지사항 내용")
                    .build();

            File file1 = new File("src/test/resources/avatar.jpg");
            File file2 = new File("src/test/resources/notice.txt");
            given(noticeService.registerNotice(any(NoticeRegisterRequestDTO.class), any(List.class))).willReturn(1L);
            MockMultipartFile data = new MockMultipartFile("data", "", APPLICATION_JSON_VALUE, mapper.writeValueAsString(registerRequestDTO).getBytes());
            MockMultipartFile attachFile1 = new MockMultipartFile("attachFiles", file1.getName(), MULTIPART_FORM_DATA_VALUE, file1.getName().getBytes());
            MockMultipartFile attachFile2 = new MockMultipartFile("attachFiles", file2.getName(), MULTIPART_FORM_DATA_VALUE, file2.getName().getBytes());

            // 실행
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .file(attachFile1)
                    .file(attachFile2)
                    .characterEncoding("UTF-8")
                    .header("Content-Type", MediaType.MULTIPART_FORM_DATA)
            );

            // 검증
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1L))
                    .andExpect(jsonPath("$.message").value(NoticeMessage.SUCCESS_NOTICE_REGISTER.getSuccessMsg()));

            verify(noticeService, times(1)).registerNotice(refEq(registerRequestDTO), any(List.class));
        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_제목이 없음")
        void failRegister_CauseOf_Insufficient_Parameter_Title() throws Exception {
            // 준비
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    //.title("공지사항 제목")
                    .content("공지사항 내용")
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // 실행
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect((jsonPath("$.errors[*].reason",
                            containsInAnyOrder(
                                    NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getMsg(),
                                    NoticeCrudErrorCode.NOTICE_TITLE_IS_NULL.getMsg()
                            )
                    )));
        }

        @ParameterizedTest
        @ValueSource(strings = {"    ", "\n", "\t", " "})
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_제목이 비어있음")
        void failRegister_CauseOf_Empty_Title(String emptyTitle) throws Exception {
            // 준비
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title(emptyTitle)
                    .content("공지사항 내용")
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // 실행
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].rejectedParameter").value(equalTo("title")))
                    .andExpect(jsonPath("$.errors[0].internalCode").value(equalTo(NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getBizCode())))
                    .andExpect(jsonPath("$.errors[0].reason").value(equalTo(NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getMsg())));
        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_내용이 없음")
        void failRegister_CauseOf_Insufficient_Parameter_Content() throws Exception {
            // 준비
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("공지사항 제목")
                    //.content("공지사항 내용")
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // 실행
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect((jsonPath("$.errors[*].reason",
                            containsInAnyOrder(
                                    NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getMsg(),
                                    NoticeCrudErrorCode.NOTICE_CONTENT_IS_NULL.getMsg()
                            )
                    )));
        }

        @ParameterizedTest
        @ValueSource(strings = {"    ", "\n", "\t", " "})
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_내용이 비어있음")
        void failRegister_CauseOf_Empty_Content(String emptyContent) throws Exception {
            // 준비
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("공지사항 제목")
                    .content(emptyContent)
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // 실행
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].rejectedParameter").value(equalTo("content")))
                    .andExpect(jsonPath("$.errors[0].internalCode").value(equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getBizCode())))
                    .andExpect(jsonPath("$.errors[0].reason").value(equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getMsg())));
        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 권한이 부족하여 실패합니다")
        void failRegister_CauseOf_Insufficient_Authorization() {

        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NoticeUpdateTest {

        private NoticeUpdateRequestDTO updateRequestDTO;

        @Test
        @DisplayName("공지사항 수정이 성공적으로 수행되어야 합니다")
        void successUpdate() throws Exception {
            // 준비
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .title("공지사항 수정된 제목")
                    .content("공지사항 수정된 내용")
                    .build();

            given(noticeService.updateNotice(any(NoticeUpdateRequestDTO.class))).willReturn(1L);

            // 실행
            ResultActions perform = mockmvc.perform(patch("/v1/notice")
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8")
                    .content(mapper.writeValueAsString(updateRequestDTO)));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1L))
                    .andExpect(jsonPath("$.message").value(NoticeMessage.SUCCESS_NOTICE_UPDATE.getSuccessMsg()));

            verify(noticeService, times(1)).updateNotice(refEq(updateRequestDTO));
        }

        @Test
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_제목이 없음")
        void failUpdate_CauseOf_Insufficient_Parameter_Title() throws Exception {
            // 준비
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    //.title("공지사항 수정된 제목")
                    .content("공지사항 수정된 내용")
                    .build();

            // 실행
            ResultActions perform = mockmvc.perform(patch("/v1/notice")
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8")
                    .content(mapper.writeValueAsString(updateRequestDTO)));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect((jsonPath("$.errors[*].reason",
                            containsInAnyOrder(
                                    NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getMsg(),
                                    NoticeCrudErrorCode.NOTICE_TITLE_IS_NULL.getMsg()
                            )
                    )));
        }

        @Test
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_내용이 없음")
        void failUpdate_CauseOf_Insufficient_Parameter_Content() throws Exception {
            // 준비
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .title("공지사항 수정된 제목")
                    //.content("공지사항 수정된 내용")
                    .build();

            // 실행
            ResultActions perform = mockmvc.perform(patch("/v1/notice")
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8")
                    .content(mapper.writeValueAsString(updateRequestDTO)));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect((jsonPath("$.errors[*].reason",
                            containsInAnyOrder(
                                    NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getMsg(),
                                    NoticeCrudErrorCode.NOTICE_CONTENT_IS_NULL.getMsg()
                            )
                    )));
        }

        @ParameterizedTest
        @MethodSource("emptyData")
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_제목이 비어있음")
        void failUpdate_CauseOf_Empty_Title(String emptyTitle) throws Exception {
            // 준비
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .title(emptyTitle)
                    .content("공지사항 수정된 내용")
                    .build();

            // 실행
            ResultActions perform = mockmvc.perform(patch("/v1/notice")
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8")
                    .content(mapper.writeValueAsString(updateRequestDTO)));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].internalCode").value(equalTo(NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getBizCode())))
                    .andExpect(jsonPath("$.errors[0].reason").value(equalTo(NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getMsg())));
        }

        @ParameterizedTest
        @MethodSource("emptyData")
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_내용이 비어있음")
        void failUpdate_CauseOf_Empty_Content(String emptyContent) throws Exception {
            // 준비
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .title("공지사항 수정된 제목")
                    .content(emptyContent)
                    .build();

            // 실행
            ResultActions perform = mockmvc.perform(patch("/v1/notice")
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8")
                    .content(mapper.writeValueAsString(updateRequestDTO)));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].internalCode").value(equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getBizCode())))
                    .andExpect(jsonPath("$.errors[0].reason").value(equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getMsg())));
        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 권한이 부족하여 실패합니다")
        void failUpdate_CauseOf_Insufficient_Authorization() {

        }

        private List<Arguments> emptyData() {
            return List.of(
                    Arguments.of("    "),
                    Arguments.of(" "),
                    Arguments.of("\n"),
                    Arguments.of("\t")
            );
        }
    }

    @Nested
    class NoticeDeleteTest {

        @Test
        @DisplayName("공지사항 삭제가 성공적으로 수행되어야 합니다")
        void successDelete() throws Exception {
            // 준비
            Long noticeId = 1L;

            doNothing().when(noticeService).removeNotice(noticeId);

            // 실행
            ResultActions perform = mockmvc.perform(delete("/v1/notice/{noticeId}", noticeId)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.errors[0].reason").value(equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getMsg())));

            verify(noticeService, times(1)).removeNotice(noticeId);
        }

        @Test
        @DisplayName("삭제하려는 공지사항이 존재하지 않아 실패합니다")
        void failDelete_CauseOf_Insufficient_Parameter_Title() throws Exception {
            // 준비
            Long noticeId = -1L;

            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND)).when(noticeService).removeNotice(noticeId);

            // 실행
            ResultActions perform = mockmvc.perform(delete("/v1/notice/{noticeId}", noticeId)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(equalTo(NoticeCrudErrorCode.NOTICE_NOT_FOUND.getMsg())));

            verify(noticeService, times(1)).removeNotice(noticeId);
        }
    }

    @Nested
    class NoticeSelectOneTest {

        @Test
        @DisplayName("공지사항 단건 조회가 성공적으로 수행되어야 합니다")
        void successSelectOne() {

        }

        @Test
        @DisplayName("단건 조회하려는 공지사항이 존재하지 않아 실패합니다")
        void failSelectOne() {

        }
    }
}
