package com.example.rsupport.api.notice.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

/**
 * 공지사항 엔티티
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
@ToString(exclude = "attachFiles")
public class Notice {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "notice_id")
    private Long id;
    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int sawCount;

    @OneToMany(mappedBy = "notice", cascade = {REMOVE}, orphanRemoval = true, fetch = LAZY)
    private List<NoticeAttachFile> attachFiles;

    public Notice() {
    }

    @Builder
    public Notice(Long id, String title, String content, LocalDateTime startTime, LocalDateTime endTime, int sawCount, List<NoticeAttachFile> attachFile) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sawCount = sawCount;
        this.attachFiles = attachFile;
    }
}
