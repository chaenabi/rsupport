package com.example.rsupport.api.notice.domain.enums;

import com.example.rsupport.api.common.SuccessMessage;
import lombok.RequiredArgsConstructor;

/**
 * 공지사항 처리 성공시 반환 메시지를 열거형으로 관리
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@RequiredArgsConstructor
public enum NoticeMessage implements SuccessMessage {
    SUCCESS_NOTICE_REGISTER("공지사항 등록이 성공적으로 완료되었습니다."),
    SUCCESS_NOTICE_UPDATE("공지사항 수정이 성공적으로 완료되었습니다."),
    SUCCESS_NOTICE_DELETE("공지사항 삭제가 성공적으로 완료되었습니다."),
    SUCCESS_NOTICE_SELECTONE("공지사항 단건 조회가 성공적으로 완료되었습니다.");

    private final String successMsg;

    @Override
    public String getSuccessMsg() {
        return successMsg;
    }
}
