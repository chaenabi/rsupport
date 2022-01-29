package com.example.rsupport.notice.domain.entity;

import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("공지사항 엔티티 테스트")
public class NoticeEntityTest {

    @Test
    @DisplayName("엔티티 생성 테스트")
    public void createEntity() {
        Notice notice = new Notice();
        assertThat(notice).isNotNull();
    }

    @Test
    @DisplayName("필드(속성) 생성 테스트")
    public void createProperties() {
        // 준비
        Long id = 1L;
        String title = "게시물 제목";
        String content = "게시물 내용";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(30);
        int sawCount = 0;
        List<NoticeAttachFile> list = List.of(new NoticeAttachFile());

        // 실행
        Notice notice = new Notice(id, title, content, startTime, endTime, sawCount, list);

        // 검증
        assertThat(notice.getId()).isEqualTo(id);
        assertThat(notice.getTitle()).isEqualTo(title);
        assertThat(notice.getStartTime()).isEqualTo(startTime);
        assertThat(notice.getEndTime()).isEqualTo(endTime);
        assertThat(notice.getSawCount()).isEqualTo(sawCount);
        assertThat(notice.getAttachFiles().get(0)).isInstanceOf(NoticeAttachFile.class);
    }
}
