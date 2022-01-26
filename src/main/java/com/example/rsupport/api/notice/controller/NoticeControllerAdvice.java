package com.example.rsupport.exception.notice;

import com.example.rsupport.exception.common.ErrorResponseDTO;
import com.example.rsupport.exception.notice.InvalidNoticeParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.rsupport.exception.common.GeneralControllerAdvice.handleValidParameterException;

/**
 * 공지사항과 관련된 작업 요청 중 발생하는 예외를 처리하는 컨트롤러 어드바이스
 *
 * @author MC Lee
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 * @created 2022-01-26
 */
@RestControllerAdvice
public class NoticeControllerAdvice {

    /**
     * # @Valid 또는 @Validated 애너테이션을 통한 검증 실패시 동작합니다.
     * # @Valid 또는 @Validated 애너테이션을 사용하는 컨트롤러 메서드에 대한 예외 응답 처리시
     * errors 인자값을 설정하면, 보다 자세한 에러메시지를 클라이언트에 전달할 수 있습니다.
     *
     * @param e InvalidParameterException
     * @return 400 (Bad Request: invalid parameter error)
     * @see ErrorResponseDTO
     */
    @ExceptionHandler(InvalidNoticeParameterException.class)
    protected ResponseEntity<ErrorResponseDTO> handleInvalidPostParameterException(InvalidNoticeParameterException e) {
        return handleValidParameterException(e.getHttpStatus(), e.getErrorCode(), e);
    }

}
/**
 * "message":"매개변수가 충분히 전달되지 못했거나 올바르지 않은 매개변수 값이 전달되었습니다.",
 * "timestamp":"2022-01-26T16:25:26.985","code":400,
 * "status":"BAD_REQUEST",
 * "errors":[{"rejectedParameter":"title","rejectedValue":null,"reason":"공지사항 제목이 반드시 전달되어야 합니다.",
 * "internalCode":-9999},{"rejectedParameter":"title","rejectedValue":null,"reason":"공지사항 제목이 비어 있으면 안됩니다.",
 * "internalCode":-9999}]
 */

/**
 * {"message":"매개변수가 충분히 전달되지 못했거나 올바르지 않은 매개변수 값이 전달되었습니다.","timestamp":"2022-01-26T16:26:08.032","code":400,"status":"BAD_REQUEST"}
 */