package com.example.rsupport.api.notice.domain.dto;

import com.example.rsupport.api.notice.domain.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.time.LocalDateTime;

/**
 * 공지사항 등록 정보를 임시로 담기 위해 사용하는 DTO
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Getter
public class NoticeRegisterRequestDTO {

    @NotNull(message = "공지사항 제목이 반드시 전달되어야 합니다.")
    @NotEmpty(message = "공지사항 제목이 비어 있으면 안됩니다.")
    private final String title;

    @NotNull(message = "공지사항 내용이 반드시 전달되어야 합니다.")
    @NotEmpty(message = "공지사항 내용이 비어 있으면 안됩니다.")
    private final String content;

    @Builder
    @ConstructorProperties({"title", "content"})
    public NoticeRegisterRequestDTO(String title, String content) {
        this.title = title != null ? title.trim() : null;
        this.content = content != null ? content.trim() : null;
    }

    public Notice toEntity() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(30);

        return Notice.builder()
                .title(title)
                .content(content)
                .startTime(LocalDateTime.of(
                        startTime.getYear(),
                        startTime.getMonth(),
                        startTime.getDayOfMonth(),
                        startTime.getHour(),
                        startTime.getMinute(),
                        startTime.getSecond()))
                .endTime(LocalDateTime.of(
                        endTime.getYear(),
                        endTime.getMonth(),
                        endTime.getDayOfMonth(),
                        endTime.getHour(),
                        endTime.getMinute(),
                        endTime.getSecond()))
                .build();
    }
}
