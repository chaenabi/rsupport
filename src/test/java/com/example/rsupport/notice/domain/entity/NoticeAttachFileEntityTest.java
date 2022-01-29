package com.example.rsupport.notice.domain.entity;

import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("공지사항 첨부파일 엔티티 테스트")
public class NoticeAttachFileEntityTest {

    @Test
    @DisplayName("엔티티 생성 테스트")
    public void createEntity() {
        NoticeAttachFile notice = new NoticeAttachFile();
        assertThat(notice).isNotNull();
    }

    @Test
    @DisplayName("필드(속성) 생성 테스트")
    public void createProperties() {
        // 준비
        Long id = 1L;
        String filename = "image.png";
        Notice notice = new Notice();

        // 실행
        NoticeAttachFile noticeAttachFile = new NoticeAttachFile(id, filename, notice);

        // 검증
        assertThat(noticeAttachFile.getId()).isEqualTo(id);
        assertThat(noticeAttachFile.getFilename()).isEqualTo(filename);
        assertThat(noticeAttachFile.getNotice()).isEqualTo(notice);
    }
}
