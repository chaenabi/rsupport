package com.example.rsupport.notice.unit;

import com.example.rsupport.api.notice.controller.NoticeController;
import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.enums.NoticeMessage;
import com.example.rsupport.api.notice.service.NoticeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 공지사항 컨트롤러 테스트
 *
 * 클라이언트로부터 전달받는 공지사항에 대한 모든 매개변수는
 * 공지사항 컨트롤러에서 모두 검증됩니다.
 * 따라서 서비스 객체부터는 추가 검증이 요구되지 않습니다.
 *
 * @author MC Lee
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 * @created 2022-01-26
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
                                                        .startTime(LocalDateTime.now())
                                                        .endTime(LocalDateTime.now().plusDays(30L))
                                                        .fileName(List.of("첨부파일1.png", "첨부파일2.zip", "첨부파일3.csv"))
                                                        .build();

            given(noticeService.registerNotice(registerRequestDTO)).willReturn(1L);

            // 실행
            ResultActions perform = mockmvc.perform(post("/v1/notice")
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8")
                    .content(mapper.writeValueAsString(registerRequestDTO)));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1L))
                    .andExpect(jsonPath("$.message").value(NoticeMessage.SUCCESS_REGISTER_NOTICE.getSuccessMsg()));
        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_제목이 없음")
        void failRegister_CauseOf_Insufficient_Parameter_Title() throws Exception {
            // 준비
            registerRequestDTO = NoticeRegisterRequestDTO.builder()
                    //.title("공지사항 제목")
                    .content("공지사항 내용")
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(30L))
                    .fileName(List.of("첨부파일1.png", "첨부파일2.zip", "첨부파일3.csv"))
                    .build();

            // 실행
            ResultActions perform = mockmvc.perform(post("/v1/notice")
                    .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=UTF-8")
                    .content(mapper.writeValueAsString(registerRequestDTO)));

            // 검증
            perform.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_내용이 없음")
        void failRegister_CauseOf_Insufficient_Parameter_Content() {

        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_제목이 비어있음")
        void failRegister_CauseOf_Empty_Title() {

        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 정보가 부족하여 실패합니다_내용이 비어있음")
        void failRegister_CauseOf_Empty_Content() {

        }

        @Test
        @DisplayName("공지사항을 등록하는데 필요한 권한이 부족하여 실패합니다")
        void failRegister_CauseOf_Insufficient_Authorization() {

        }
    }

    @Nested
    class NoticeUpdateTest {

        @Test
        @DisplayName("공지사항 수정이 성공적으로 수행되어야 합니다")
        void successUpdate() {

        }

        @Test
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_제목이 없음")
        void failUpdate_CauseOf_Insufficient_Parameter_Title() {

        }

        @Test
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_내용이 없음")
        void failUpdate_CauseOf_Insufficient_Parameter_Content() {

        }

        @Test
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_제목이 비어있음")
        void failRegister_CauseOf_Empty_Title() {

        }

        @Test
        @DisplayName("공지사항을 수정하는데 필요한 정보가 부족하여 실패합니다_내용이 비어있음")
        void failRegister_CauseOf_Empty_Content() {

        }
    }

    @Nested
    class NoticeDeleteTest {

        @Test
        @DisplayName("공지사항 삭제가 성공적으로 수행되어야 합니다")
        void successDelete() {

        }

        @Test
        @DisplayName("삭제하려는 공지사항이 이미 존재하지 않아 실패합니다")
        void failDelete_CauseOf_Insufficient_Parameter_Title() {

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
