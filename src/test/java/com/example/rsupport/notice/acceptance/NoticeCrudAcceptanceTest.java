package com.example.rsupport.notice.acceptance;

import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeUpdateRequestDTO;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.util.List;

import static com.example.rsupport.api.notice.domain.enums.NoticeMessage.*;
import static com.example.rsupport.exception.common.controllerAdvice.GeneralParameterErrorCode.INVALID_PARAMETER;
import static com.example.rsupport.exception.notice.NoticeCrudErrorCode.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

/**
 * 공지사항 CRUD 인수 및 통합테스트
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureRestDocs
@DisplayName("공지사항 CRUD 인수(통합) 테스트")
public class NoticeCrudAcceptanceTest {

    @LocalServerPort
    int port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder().addFilter(
                        documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("공지사항 등록 테스트")
    class NoticeRegisterTest {

        private NoticeRegisterRequestDTO requestDTO;
        private final String noticeTitle = "notice title";
        private final String noticeContent = "notice content";

        @Test
        @DisplayName("공지사항 등록 성공_첨부파일 미포함")
        void successRegisterNotice_Without_AttachFile() {
            // 준비
            requestDTO = NoticeRegisterRequestDTO.builder()
                    .title(noticeTitle)
                    .content(noticeContent)
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice");

            // 검증
            when.then()
                    .statusCode(OK.value())
                    .assertThat()
                    .body("data", equalTo(1)).log().all()
                    .body("message", equalTo(SUCCESS_NOTICE_REGISTER.getSuccessMsg())).log().all();
        }

        @Test
        @DisplayName("공지사항 등록 성공_다중 첨부파일 포함")
        void successRegisterNotice_With_AttachFiles() {
            // 준비
            requestDTO = NoticeRegisterRequestDTO.builder()
                    .title(noticeTitle)
                    .content(noticeContent)
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/avatar.jpg"), MULTIPART_FORM_DATA_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/notice.zip"), MULTIPART_FORM_DATA_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice");

            // 검증
            when.then()
                    .statusCode(OK.value())
                    .assertThat()
                    .body("data", equalTo(1)).log().all()
                    .body("message", equalTo(SUCCESS_NOTICE_REGISTER.getSuccessMsg())).log().all();
        }


        @Test
        @DisplayName("공지사항 등록 실패_제목이 없음")
        void failRegister_No_Title() {
            // 준비
            requestDTO = NoticeRegisterRequestDTO.builder()
                    //.title("notice title")
                    .content("notice content")
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/avatar.jpg"), MULTIPART_FORM_DATA_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/notice.zip"), MULTIPART_FORM_DATA_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice");

            // 검증
            when.then()
                    .statusCode(BAD_REQUEST.value())
                    .assertThat()
                    .body("message", equalTo(INVALID_PARAMETER.getMsg()))
                    .body("code", equalTo(NOTICE_TITLE_IS_NULL.getHttpStatus().value()));

            List<String> list = when.then()
                    .extract()
                    .jsonPath()
                    .getList("errors.reason");

            assertThat(list).containsExactlyInAnyOrder(NOTICE_TITLE_IS_NULL.getMsg(), NOTICE_TITLE_IS_EMPTY.getMsg());
        }

        @Test
        @DisplayName("공지사항 등록 실패_내용이 없음")
        void failRegisterNotice_No_Content() {
            // 준비
            requestDTO = NoticeRegisterRequestDTO.builder()
                    .title("notice title")
                    //.content("notice content")
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/avatar.jpg"), MULTIPART_FORM_DATA_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/notice.zip"), MULTIPART_FORM_DATA_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice");

            // 검증
            when.then()
                    .statusCode(BAD_REQUEST.value())
                    .assertThat()
                    .body("message", equalTo(INVALID_PARAMETER.getMsg()))
                    .body("code", equalTo(NOTICE_CONTENT_IS_NULL.getHttpStatus().value()));

            List<String> list = when.then()
                    .extract()
                    .jsonPath()
                    .getList("errors.reason");

            assertThat(list).containsExactlyInAnyOrder(NOTICE_CONTENT_IS_NULL.getMsg(), NOTICE_CONTENT_IS_EMPTY.getMsg());
        }
    }

    @Nested
    @DisplayName("공지사항 수정 테스트")
    @SuppressWarnings("all")
    class NoticeUpdateTest {

        private NoticeUpdateRequestDTO requestDTO;
        private final Long 수정을원하는공지사항번호 = 1L;
        private final String 수정된공지사항제목 = "수정된공지사항제목";
        private final String 수정된공지사항내용 = "수정된공지사항내용";

        @BeforeEach
        void setUp() {
            // 수정하기 위한 공지사항을 먼저 등록하고자 실행합니다.
            new NoticeCrudAcceptanceTest.NoticeRegisterTest().successRegisterNotice_With_AttachFiles();
        }

        @Test
        @DisplayName("공지사항 등록 성공_첨부파일 미포함")
        void successRegisterNotice_Without_AttachFile() {
            // 준비
            requestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(수정을원하는공지사항번호)
                    .title(수정된공지사항제목)
                    .content(수정된공지사항내용)
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice-update");

            // 검증
            when.then()
                    .statusCode(OK.value())
                    .assertThat()
                    .body("data", equalTo(1)).log().all()
                    .body("message", equalTo(SUCCESS_NOTICE_UPDATE.getSuccessMsg())).log().all();
        }

        @Test
        @DisplayName("공지사항 수정 성공_다중 첨부파일 포함")
        void successUpdateNotice() {
            // 준비
            requestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(수정을원하는공지사항번호)
                    .title(수정된공지사항제목)
                    .content(수정된공지사항내용)
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/avatar.jpg"), MULTIPART_FORM_DATA_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/notice.zip"), MULTIPART_FORM_DATA_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice-update");

            // 검증
            when.then()
                    .statusCode(OK.value())
                    .assertThat()
                    .body("data", equalTo(1)).log().all()
                    .body("message", equalTo(SUCCESS_NOTICE_UPDATE.getSuccessMsg())).log().all();
        }

        @Test
        @DisplayName("공지사항 수정 실패_매개 변수 부족_공지사항 아이디가 없음")
        void failUpdateNotice_No_Id() {
            // 준비
            requestDTO = NoticeUpdateRequestDTO.builder()
                    // .noticeId(수정을원하는공지사항번호)
                    .title(수정된공지사항제목)
                    .content(수정된공지사항내용)
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/avatar.jpg"), MULTIPART_FORM_DATA_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/notice.zip"), MULTIPART_FORM_DATA_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice-update");

            // 검증
            when.then()
                    .statusCode(BAD_REQUEST.value())
                    .assertThat()
                    .body("message", equalTo(INVALID_PARAMETER.getMsg()))
                    .body("errors[0].reason", equalTo(NOTICE_ID_IS_NULL.getMsg())).log().all()
                    .body("errors[0].internalCode", equalTo(NOTICE_ID_IS_NULL.findMatchBizCode(NOTICE_ID_IS_NULL.getMsg()))).log().all();
        }

        @Test
        @DisplayName("공지사항 수정 실패_매개 변수 부족_공지사항 제목이 없음")
        void failUpdateNotice_No_Title() {
            // 준비
            requestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(수정을원하는공지사항번호)
                    //.title(수정된공지사항제목)
                    .content(수정된공지사항내용)
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/avatar.jpg"), MULTIPART_FORM_DATA_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/notice.zip"), MULTIPART_FORM_DATA_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice-update");

            // 검증
            List<String> list = when.then()
                    .statusCode(BAD_REQUEST.value())
                    .extract()
                    .jsonPath()
                    .getList("errors.reason");

            assertThat(list).containsExactlyInAnyOrder(NOTICE_TITLE_IS_NULL.getMsg(), NOTICE_TITLE_IS_EMPTY.getMsg());
        }

        @Test
        @DisplayName("공지사항 수정 실패_매개 변수 부족_공지사항 내용이 없음")
        void failUpdateNotice_No_Content() {
            // 준비
            requestDTO = NoticeUpdateRequestDTO.builder()
                    .noticeId(수정을원하는공지사항번호)
                    .title(수정된공지사항제목)
                    //.content(수정된공지사항내용)
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"))
                    .multiPart("data", new Gson().toJson(requestDTO), APPLICATION_JSON_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/avatar.jpg"), MULTIPART_FORM_DATA_VALUE)
                    .multiPart("attachFiles", new File("src/test/resources/notice.zip"), MULTIPART_FORM_DATA_VALUE);

            // 실행
            Response when = given.when()
                    .post("/v1/notice-update");

            // 검증
            List<String> list = when.then()
                    .statusCode(BAD_REQUEST.value())
                    .extract()
                    .jsonPath()
                    .getList("errors.reason");

            assertThat(list).containsExactlyInAnyOrder(NOTICE_CONTENT_IS_NULL.getMsg(), NOTICE_CONTENT_IS_EMPTY.getMsg());
        }
    }

    @Nested
    @DisplayName("공지사항 삭제 테스트")
    @SuppressWarnings("all")
    class NoticeDeleteTest {

        private final Long wantToDeleteNoticeId = 1L;

        @BeforeEach
        void setUp() {
            // 삭제하기 위한 공지사항을 먼저 등록하고자 실행합니다.
            new NoticeCrudAcceptanceTest.NoticeRegisterTest().successRegisterNotice_With_AttachFiles();
        }

        @Test
        @DisplayName("공지사항 삭제 성공")
        void successDeleteNotice() {
            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"));

            // 실행
            Response when = given.when()
                    .delete("/v1/notice/{noticeId}", wantToDeleteNoticeId);

            // 검증
            when.then()
                    .statusCode(OK.value())
                    .assertThat()
                    .body("message", equalTo(SUCCESS_NOTICE_DELETE.getSuccessMsg()));
        }

        @Test
        @DisplayName("공지사항 삭제 실패")
        void failDeleteNotice() {
            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"));

            // 실행
            Response when = given.when()
                    .delete("/v1/notice/{noticeId}", -1);

            // 검증
            when.then()
                    .statusCode(NOT_FOUND.value())
                    .assertThat()
                    .body("message", equalTo(NOTICE_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("공지사항 단건 조회 테스트")
    @SuppressWarnings("all")
    class NoticeSelectOneTest {

        private final Long wantToFindNoticeId = 1L;

        @BeforeEach
        void setUp() {
            // 조회하기 위한 공지사항을 먼저 등록하고자 실행합니다.
            new NoticeCrudAcceptanceTest.NoticeRegisterTest().successRegisterNotice_With_AttachFiles();
        }

        @Test
        @DisplayName("공지사항 단건 조회 성공")
        void successSelectOneNotice() {
            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"));

            // 실행
            Response when = given.when()
                    .get("/v1/notice/{noticeId}", wantToFindNoticeId);

            // 검증
            when.then()
                    .statusCode(OK.value())
                    .assertThat()
                    .body("data.noticeId", equalTo(wantToFindNoticeId.intValue()))
                    .body("message", equalTo(SUCCESS_NOTICE_SELECTONE.getSuccessMsg())).log().all();
        }

        @Test
        @DisplayName("공지사항 단건 조회 실패")
        void failSelectOneNotice() {
            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .filter(document("notice"));

            // 실행
            Response when = given.when()
                    .get("/v1/notice/{noticeId}", -1);

            // 검증
            when.then()
                    .statusCode(NOT_FOUND.value())
                    .assertThat()
                    .body("message", equalTo(NOTICE_NOT_FOUND.getMsg()));
        }
    }
}
