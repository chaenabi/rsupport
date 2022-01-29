package com.example.rsupport.api.notice.domain.dto;

import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import lombok.Builder;
import lombok.Getter;

/**
 * 공지사항 첨부파일 DTO
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Getter
public class NoticeAttachFileDTO {
    private final Long id;
    private final String filename;
    private final Notice notice;

    @Builder
    public NoticeAttachFileDTO(Long id, String filename, Notice notice) {
        this.id = id;
        this.filename = filename;
        this.notice = notice;
    }

    public NoticeAttachFile toEntity() {
        return NoticeAttachFile.builder()
                .notice(getNotice())
                .filename(getFilename())
                .build();
    }
}
