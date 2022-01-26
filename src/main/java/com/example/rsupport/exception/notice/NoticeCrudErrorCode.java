package com.example.rsupport.exception.notice;

import com.example.rsupport.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum NoticeCrudErrorCode implements ErrorCode {

    NOTICE_CRUD_FAIL(HttpStatus.BAD_REQUEST, -999, "공지사항 관련 처리 요청이 실패했습니다."),
    NOTICE_TITLE_IS_NULL(HttpStatus.BAD_REQUEST, -2, "공지사항 제목이 반드시 전달되어야 합니다."),
    NOTICE_TITLE_IS_EMPTY(HttpStatus.BAD_REQUEST, -3, "공지사항 제목이 비어 있으면 안됩니다."),
    NOTICE_CONTENT_IS_NULL(HttpStatus.BAD_REQUEST, -4, "공지사항 내용이 반드시 전달되어야 합니다."),
    NOTICE_CONTENT_IS_EMPTY(HttpStatus.BAD_REQUEST, -5, "공지사항 내용이 비어 있으면 안됩니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, -6, "해당 공지사항은 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final Integer bizCode;
    private final String msg;

    public Integer findMatchBizCode(final String failMessage) {
        return Arrays.stream(NoticeCrudErrorCode.values())
                .filter(errorCode -> (errorCode.msg).equals(failMessage))
                .map(NoticeCrudErrorCode::getBizCode)
                .findAny()
                .orElse(-9999);
    }
}
