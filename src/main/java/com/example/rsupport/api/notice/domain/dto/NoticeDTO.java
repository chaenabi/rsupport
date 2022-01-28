package com.example.rsupport.api.notice.domain.dto;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeDTO {

    private final Long noticeId;
    private final String title;
    private final String content;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endTime;
    private final int sawCount;

    @Builder
    @JsonIncludeProperties({"noticeId", "title", "content", "sawCount"})
    public NoticeDTO(Long noticeId, String title, String content, int sawCount, LocalDateTime startTime, LocalDateTime endTime) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.sawCount = sawCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
