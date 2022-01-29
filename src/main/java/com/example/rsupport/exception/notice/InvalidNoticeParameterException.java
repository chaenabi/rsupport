package com.example.rsupport.exception.notice;

import com.example.rsupport.exception.common.ErrorCode;
import com.example.rsupport.exception.common.InvalidParameterException;
import org.springframework.validation.Errors;

/**
 * 공지사항 컨트롤러에 전달된 파라매터를 검증 후 발생한 에러를 처리하는 예외 클래스
 *
 * @author MC Lee
 * @created 2022-01-29
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
public class InvalidNoticeParameterException extends InvalidParameterException {
    public InvalidNoticeParameterException(Errors errors, ErrorCode errorCode) {
        super(errors, errorCode);
    }
}
