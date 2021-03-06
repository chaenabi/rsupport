package com.example.rsupport.notice.unit;

import com.example.rsupport.api.notice.controller.NoticeController;
import com.example.rsupport.api.notice.domain.dto.NoticeDTO;
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
import java.time.LocalDateTime;
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
 * ???????????? ???????????? ?????????
 * <p>
 * ???????????????????????? ???????????? ??????????????? ?????? ?????? ???????????????
 * ???????????? ?????????????????? ?????? ???????????????.
 * ????????? ????????? ??????????????? ?????? ????????? ???????????? ????????????.
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@WebMvcTest(controllers = NoticeController.class)
@DisplayName("???????????? ???????????? ?????????")
public class NoticeControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private NoticeService noticeService;

    @Nested
    @DisplayName("???????????? ?????? ?????????")
    class NoticeRegisterTest {

        private NoticeRegisterRequestDTO registerRequestDTO;

        @Test
        @DisplayName("???????????? ????????? ??????????????? ??????????????? ?????????")
        @SuppressWarnings("all")
        void successRegister() throws Exception {
            // ??????
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("???????????? ??????")
                    .content("???????????? ??????")
                    .build();

            given(noticeService.registerNotice(any(NoticeRegisterRequestDTO.class), any(List.class))).willReturn(1L);
            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    "application/json",
                    mapper.writeValueAsString(registerRequestDTO).getBytes());

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .characterEncoding("UTF-8")
                    .header("Content-Type", MediaType.MULTIPART_FORM_DATA)
            );

            // ??????
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1L))
                    .andExpect(jsonPath("$.message").value(NoticeMessage.SUCCESS_NOTICE_REGISTER.getSuccessMsg()));

            verify(noticeService, times(1)).registerNotice(refEq(registerRequestDTO), any(List.class));
        }

        @Test
        @DisplayName("???????????? ????????? ??????????????? ??????????????? ?????????_?????? ???????????? ??????")
        @SuppressWarnings("all")
        void successRegisterIncludeAttachFiles() throws Exception {
            // ??????
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("???????????? ??????")
                    .content("???????????? ??????")
                    .build();

            File file1 = new File("src/test/resources/avatar.jpg");
            File file2 = new File("src/test/resources/notice.txt");
            given(noticeService.registerNotice(any(NoticeRegisterRequestDTO.class), any(List.class))).willReturn(1L);
            MockMultipartFile data = new MockMultipartFile("data", "", APPLICATION_JSON_VALUE, mapper.writeValueAsString(registerRequestDTO).getBytes());
            MockMultipartFile attachFile1 = new MockMultipartFile("attachFiles", file1.getName(), MULTIPART_FORM_DATA_VALUE, file1.getName().getBytes());
            MockMultipartFile attachFile2 = new MockMultipartFile("attachFiles", file2.getName(), MULTIPART_FORM_DATA_VALUE, file2.getName().getBytes());

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .file(attachFile1)
                    .file(attachFile2)
                    .characterEncoding("UTF-8")
                    .header("Content-Type", MediaType.MULTIPART_FORM_DATA)
            );

            // ??????
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1L))
                    .andExpect(jsonPath("$.message").value(NoticeMessage.SUCCESS_NOTICE_REGISTER.getSuccessMsg()));

            verify(noticeService, times(1)).registerNotice(refEq(registerRequestDTO), any(List.class));
        }

        @Test
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ??????")
        void failRegister_CauseOf_Insufficient_Parameter_Title() throws Exception {
            // ??????
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    //.title("???????????? ??????")
                    .content("???????????? ??????")
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
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
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ????????????")
        void failRegister_CauseOf_Empty_Title(String emptyTitle) throws Exception {
            // ??????
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title(emptyTitle)
                    .content("???????????? ??????")
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].rejectedParameter").value(equalTo("title")))
                    .andExpect(jsonPath("$.errors[0].internalCode").value(equalTo(NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getBizCode())))
                    .andExpect(jsonPath("$.errors[0].reason").value(equalTo(NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getMsg())));
        }

        @Test
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ??????")
        void failRegister_CauseOf_Insufficient_Parameter_Content() throws Exception {
            // ??????
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("???????????? ??????")
                    //.content("???????????? ??????")
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
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
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ????????????")
        void failRegister_CauseOf_Empty_Content(String emptyContent) throws Exception {
            // ??????
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    .title("???????????? ??????")
                    .content(emptyContent)
                    .build();

            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(registerRequestDTO).getBytes()
            );

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice")
                    .file(data)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].rejectedParameter").value(equalTo("content")))
                    .andExpect(jsonPath("$.errors[0].internalCode").value(equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getBizCode())))
                    .andExpect(jsonPath("$.errors[0].reason").value(equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getMsg())));
        }

    }

    @Nested
    @DisplayName("???????????? ?????? ?????????")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NoticeUpdateTest {

        private NoticeUpdateRequestDTO updateRequestDTO;

        @Test
        @DisplayName("???????????? ????????? ??????????????? ??????????????? ?????????")
        @SuppressWarnings("all")
        void successUpdate() throws Exception {
            // ??????
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(1L)
                    .title("????????? ???????????? ??????")
                    .content("????????? ???????????? ??????")
                    .build();

            File patchFile = new File("src/test/resources/avatar.jpg");
            given(noticeService.updateNotice(any(NoticeUpdateRequestDTO.class), any(List.class))).willReturn(1L);
            MockMultipartFile data = new MockMultipartFile("data", "", APPLICATION_JSON_VALUE, mapper.writeValueAsString(updateRequestDTO).getBytes());
            MockMultipartFile attachFile = new MockMultipartFile("attachFiles", patchFile.getName(), MULTIPART_FORM_DATA_VALUE, patchFile.getName().getBytes());

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice-update")
                    .file(data)
                    .file(attachFile)
                    .characterEncoding("UTF-8")
                    .header("Content-Type", MediaType.MULTIPART_FORM_DATA)
            );

            // ??????
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1L))
                    .andExpect(jsonPath("$.message").value(NoticeMessage.SUCCESS_NOTICE_UPDATE.getSuccessMsg()));

            verify(noticeService, times(1)).updateNotice(refEq(updateRequestDTO), any(List.class));
        }

        @Test
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_???????????? ??????????????? ??????")
        void failUpdate_CauseOf_Insufficient_Parameter_NoticeId() throws Exception {
            // ??????
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    //.noticeId(1L)
                    .title("???????????? ????????? ??????")
                    .content("???????????? ????????? ??????")
                    .build();
            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(updateRequestDTO).getBytes());


            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice-update")
                    .file(data));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect((jsonPath("$.errors[0].reason", equalTo(NoticeCrudErrorCode.NOTICE_ID_IS_NULL.getMsg()))));
        }

        @Test
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ??????")
        void failUpdate_CauseOf_Insufficient_Parameter_Title() throws Exception {
            // ??????
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(1L)
                    //.title("???????????? ????????? ??????")
                    .content("???????????? ????????? ??????")
                    .build();
            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "", APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(updateRequestDTO).getBytes());

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice-update")
                    .file(data));

            // ??????
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
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ??????")
        void failUpdate_CauseOf_Insufficient_Parameter_Content() throws Exception {
            // ??????
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(1L)
                    .title("???????????? ????????? ??????")
                    //.content("???????????? ????????? ??????")
                    .build();
            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "",
                    APPLICATION_JSON_VALUE, mapper.writeValueAsString(updateRequestDTO).getBytes());

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice-update")
                    .file(data));

            // ??????
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
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ????????????")
        void failUpdate_CauseOf_Empty_Title(String emptyTitle) throws Exception {
            // ??????
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(1L)
                    .title(emptyTitle)
                    .content("???????????? ????????? ??????")
                    .build();
            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "", APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(updateRequestDTO).getBytes());

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice-update")
                    .file(data));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect((jsonPath("$.errors[0].reason", equalTo(NoticeCrudErrorCode.NOTICE_TITLE_IS_EMPTY.getMsg()))));
        }

        @ParameterizedTest
        @MethodSource("emptyData")
        @DisplayName("??????????????? ??????????????? ????????? ????????? ???????????? ???????????????_????????? ????????????")
        void failUpdate_CauseOf_Empty_Content(String emptyContent) throws Exception {
            // ??????
            updateRequestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(1L)
                    .title("???????????? ????????? ??????")
                    .content(emptyContent)
                    .build();
            MockMultipartFile data = new MockMultipartFile(
                    "data",
                    "", APPLICATION_JSON_VALUE,
                    mapper.writeValueAsString(updateRequestDTO).getBytes());

            // ??????
            ResultActions perform = mockmvc.perform(multipart("/v1/notice-update")
                    .file(data));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect((jsonPath("$.errors[0].reason", equalTo(NoticeCrudErrorCode.NOTICE_CONTENT_IS_EMPTY.getMsg()))));
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
    @DisplayName("???????????? ?????? ?????????")
    class NoticeDeleteTest {

        @Test
        @DisplayName("???????????? ????????? ??????????????? ??????????????? ?????????")
        void successDelete() throws Exception {
            // ??????
            Long noticeId = 1L;

            doNothing().when(noticeService).removeNotice(noticeId);

            // ??????
            ResultActions perform = mockmvc.perform(delete("/v1/notice/{noticeId}", noticeId)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("message").value(equalTo(NoticeMessage.SUCCESS_NOTICE_DELETE.getSuccessMsg())));

            verify(noticeService, times(1)).removeNotice(noticeId);
        }

        @Test
        @DisplayName("??????????????? ??????????????? ???????????? ?????? ???????????????")
        void failDelete_CauseOf_Insufficient_Parameter_Title() throws Exception {
            // ??????
            Long noticeId = -1L;

            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND)).when(noticeService).removeNotice(noticeId);

            // ??????
            ResultActions perform = mockmvc.perform(delete("/v1/notice/{noticeId}", noticeId)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(equalTo(NoticeCrudErrorCode.NOTICE_NOT_FOUND.getMsg())));

            verify(noticeService, times(1)).removeNotice(noticeId);
        }
    }

    @Nested
    @DisplayName("???????????? ?????? ?????? ?????????")
    class NoticeSelectOneTest {

        @Test
        @DisplayName("???????????? ?????? ????????? ??????????????? ??????????????? ?????????")
        void successSelectOne() throws Exception {
            // ??????
            Long noticeId = 1L;
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.plusDays(30);

            NoticeDTO noticeDTO = NoticeDTO.builder()
                    .noticeId(noticeId)
                    .title("????????? ???????????? ??????")
                    .content("????????? ???????????? ??????")
                    .startTime(LocalDateTime.of(
                            startTime.getYear(),
                            startTime.getMonth(),
                            startTime.getDayOfMonth(),
                            startTime.getHour(),
                            startTime.getMinute(),
                            startTime.getSecond()))
                    .endTime(LocalDateTime.of(
                            endTime.getYear(),
                            endTime.getMonth(),
                            endTime.getDayOfMonth(),
                            endTime.getHour(),
                            endTime.getMinute(),
                            endTime.getSecond()))
                    .sawCount(1)
                    .build();

            given(noticeService.selectNoticeOne(noticeId)).willReturn(noticeDTO);

            // ??????
            ResultActions perform = mockmvc.perform(get("/v1/notice/{noticeId}", noticeId)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("message").value(equalTo(NoticeMessage.SUCCESS_NOTICE_SELECTONE.getSuccessMsg())))
                    .andExpect(jsonPath("data.noticeId").value(equalTo(1)))
                    .andExpect(jsonPath("data.title").value(equalTo(noticeDTO.getTitle())))
                    .andExpect(jsonPath("data.content").value(equalTo(noticeDTO.getContent())))
                    .andExpect(jsonPath("data.startTime").value(equalTo(noticeDTO.getStartTime().toString())))
                    .andExpect(jsonPath("data.endTime").value(equalTo(noticeDTO.getEndTime().toString())))
                    .andExpect(jsonPath("data.sawCount").value(equalTo(1)));
            verify(noticeService, times(1)).selectNoticeOne(noticeId);
        }

        @Test
        @DisplayName("?????? ??????????????? ??????????????? ???????????? ?????? ???????????????")
        void failSelectOne() throws Exception {
            // ??????
            Long noticeId = -1L;
            doThrow(new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND)).when(noticeService).selectNoticeOne(noticeId);

            // ??????
            ResultActions perform = mockmvc.perform(get("/v1/notice/{noticeId}", noticeId)
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8"));

            // ??????
            perform.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(equalTo(NoticeCrudErrorCode.NOTICE_NOT_FOUND.getMsg())));
        }
    }
}
