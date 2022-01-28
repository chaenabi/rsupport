package com.example.rsupport.exception.common;

import com.example.rsupport.exception.common.controllerAdvice.GeneralParameterErrorCode;
import lombok.Getter;
import org.springframework.validation.Errors;

/**
 * - @Valid 검증 중 발생하는 error를 핸들링하는 예외 클래스
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Getter
public abstract class InvalidParameterException extends BizException {

    private final Errors errors;
    private final ErrorCode errorCode;

    public InvalidParameterException(Errors errors, ErrorCode errorCode) {
        super(GeneralParameterErrorCode.INVALID_PARAMETER);
        this.errors = errors;
        this.errorCode = errorCode;
    }
}
