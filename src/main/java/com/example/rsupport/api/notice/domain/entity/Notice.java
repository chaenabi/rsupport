package com.example.rsupport.api.notice.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

@Entity
@Table
@Getter
@Setter
@ToString(exclude = "attachFile")
public class Notice {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "notice_id")
    private Long id;

    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "notice", cascade = {PERSIST, REMOVE}, orphanRemoval = true, fetch = LAZY)
    private List<NoticeAttachFile> attachFile;

    public Notice() {
    }

    @Builder
    public Notice(Long id, String title, String content, LocalDateTime startTime, LocalDateTime endTime, List<NoticeAttachFile> attachFile) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attachFile = attachFile;
    }
}
