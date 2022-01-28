package com.example.rsupport.api.notice.domain.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;

@Getter
public class NoticeUpdateRequestDTO {

    @NotNull(message = "공지사항 번호가 반드시 전달되어야 합니다.")
    @NotEmpty(message = "공지사항 번호가 비어 있으면 안됩니다.")
    private final Long noticeId;

    @NotNull(message = "공지사항 제목이 반드시 전달되어야 합니다.")
    @NotEmpty(message = "공지사항 제목이 비어 있으면 안됩니다.")
    private final String title;

    @NotNull(message = "공지사항 내용이 반드시 전달되어야 합니다.")
    @NotEmpty(message = "공지사항 내용이 비어 있으면 안됩니다.")
    private final String content;

    @Builder
    @ConstructorProperties({"noticeId", "title", "content", "fileName"})
    public NoticeUpdateRequestDTO(Long noticeId, String title, String content) {
        this.noticeId = noticeId;
        this.title = title != null ? title.trim() : null;
        this.content = content != null ? content.trim() : null;
    }
}
