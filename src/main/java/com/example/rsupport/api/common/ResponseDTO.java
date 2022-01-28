package com.example.rsupport.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 컨트롤러에서 반환하는 결과 데이터를, 일관된 포맷형식으로 클라이언트에게 전달하기 위한 클래스
 *
 * @param <T> 컨트롤러에서 반환하고자 하는 데이터타입
 * @author MC Lee
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Getter
public class ResponseDTO<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    private final String message;
    private final HttpStatus httpStatus;

    public ResponseDTO(SuccessMessage message, HttpStatus httpStatus) {
        this.message = message.getSuccessMsg();
        this.httpStatus = httpStatus;
    }

    public ResponseDTO(T data, SuccessMessage message, HttpStatus httpStatus) {
        this.data = data;
        this.message = message.getSuccessMsg();
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
