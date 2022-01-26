package com.example.rsupport.api.notice.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NoticeRegisterRequestDTO {

    @NotNull(message = "공지사항 제목이 반드시 전달되어야 합니다.")
    @NotEmpty(message = "공지사항 제목이 비어 있으면 안됩니다.")
    private final String title;

    @NotNull(message = "공지사항 내용이 반드시 전달되어야 합니다.")
    @NotEmpty(message = "공지사항 내용이 비어 있으면 안됩니다.")
    private final String content;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime startTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime endTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<String> fileName;

    @Builder
    @ConstructorProperties({"title", "content", "startTime", "endTime", "fileName"})
    public NoticeRegisterRequestDTO(String title, String content, LocalDateTime startTime, LocalDateTime endTime, List<String> fileName) {
        this.title = title != null ? title.trim() : null;
        this.content = content != null ? content.trim() : null;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fileName = fileName;
    }
}
