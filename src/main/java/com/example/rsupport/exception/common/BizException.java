package com.example.rsupport.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 클라이언트 측에 반환하는 에러를 처리하는 예외 처리 클래스입니다.
 * BizException을 상속하는 InvalidParameterException과의 차이점: <br>
 * BizException은 미리 협의된 내부 에러코드(Internal Server errorCode)를 가지지 않습니다.
 *
 * @author MC Lee
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 * @created 2022-01-26
 */
@Getter
public class BizException extends RuntimeException {
    private final String message;
    private final Integer bizCode;
    private final HttpStatus httpStatus;

    public BizException(ErrorCode code) {
        //super(code.getMsg(), new Throwable(code.getHttpStatus().getReasonPhrase()));
        this.message = code.getMsg();
        this.bizCode = code.getBizCode();
        this.httpStatus = code.getHttpStatus();
    }
}
