package com.example.rsupport.api.notice.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

/**
 * 공지사항 첨부파일 엔티티
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Entity
@Table
@Getter
@Setter
public class NoticeAttachFile {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "attach_file_id")
    private Long id;

    private String filename;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public NoticeAttachFile() {
    }

    @Builder
    public NoticeAttachFile(Long id, String filename, Notice notice) {
        this.id = id;
        this.filename = filename;
        this.notice = notice;
    }
}
