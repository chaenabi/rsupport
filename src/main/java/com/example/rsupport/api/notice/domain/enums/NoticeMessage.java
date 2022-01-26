package com.example.rsupport.api.notice.domain.enums;

import com.example.rsupport.api.notice.common.SuccessMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NoticeMessage implements SuccessMessage {
    SUCCESS_REGISTER_NOTICE("공지사항 등록이 성공적으로 완료되었습니다.");

    private final String successMsg;

    @Override
    public String getSuccessMsg() {
        return successMsg;
    }
}
