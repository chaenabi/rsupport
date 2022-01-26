package com.example.rsupport.notice.acceptance;

import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.enums.NoticeMessage;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
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
@AutoConfigureRestDocs
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

        private NoticeRegisterRequestDTO registerDTO;

        @Test
        @DisplayName("공지사항 등록 성공")
        void successRegisterNotice() {
            // 준비
            registerDTO = NoticeRegisterRequestDTO.builder()
                    .title("공지사항 제목")
                    .content("공지사항 내용")
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(30L))
                    .fileName(List.of("첨부파일1.png", "첨부파일2.zip", "첨부파일3.csv"))
                    .build();

            RequestSpecification given = given(NoticeCrudAcceptanceTest.this.spec)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(ContentType.JSON)
                    .filter(document("notice"))
                    .body(registerDTO).log().all();

            // 실행
            Response when = given.when()
                    .post("/v1/notice");

            // 검증
            when.then()
                    .statusCode(HttpStatus.OK.value())
                    .assertThat()
                    .body("data", equalTo(1)).log().all()
                    .body("message", equalTo(NoticeMessage.SUCCESS_REGISTER_NOTICE.getSuccessMsg())).log().all();
        }
    }

}
