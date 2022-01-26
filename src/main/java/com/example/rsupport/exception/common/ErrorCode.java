package com.example.rsupport.exception.common;

import org.springframework.http.HttpStatus;

/**
 * 에러 목록을 열거형으로 가지는 클래스들을 확장하기 위한 인터페이스
 * findMatchBizCode는
 * 열거형 목록 중 하나의 msg 또는 httpStatus와 일치하는 값을 찾아 bizCode를 반환하도록 구현해야 합니다.
 *
 * @author MC Lee
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 * @created 2022-01-26
 */
public interface ErrorCode {
    String getMsg();
    Integer getBizCode();
    HttpStatus getHttpStatus();
    Integer findMatchBizCode(String failMessage);
}