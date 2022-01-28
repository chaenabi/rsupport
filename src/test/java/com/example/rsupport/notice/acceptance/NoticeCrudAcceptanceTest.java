package com.example.rsupport.notice.acceptance;

import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.enums.NoticeMessage;
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

import static com.example.rsupport.exception.common.controllerAdvice.GeneralParameterErrorCode.INVALID_PARAMETER;
import static com.example.rsupport.exception.notice.NoticeCrudErrorCode.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
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
    class RegisterNoticeTest {

        private NoticeRegisterRequestDTO requestDTO;

        @Test
        @DisplayName("공지사항 등록 성공_첨부파일 미포함")
        void successRegisterNotice_Without_AttachFile() {
            // 준비
            requestDTO = NoticeRegisterRequestDTO.builder()
                    .title("notice title")
                    .content("notice content")
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
                    .body("message", equalTo(NoticeMessage.SUCCESS_NOTICE_REGISTER.getSuccessMsg())).log().all();
        }

        @Test
        @DisplayName("공지사항 등록 성공_다중 첨부파일 포함")
        void successRegisterNotice_With_AttachFiles() {
            // 준비
            requestDTO = NoticeRegisterRequestDTO.builder()
                    .title("notice title")
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
                    .statusCode(OK.value())
                    .assertThat()
                    .body("data", equalTo(1)).log().all()
                    .body("message", equalTo(NoticeMessage.SUCCESS_NOTICE_REGISTER.getSuccessMsg())).log().all();
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
}
