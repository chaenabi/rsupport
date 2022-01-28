package com.example.rsupport.api.notice.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

@Entity
@Table
@Getter
@Setter
public class NoticeAttachFile {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "attach_file_id")
    private Long id;

    private String filePath;

    @ManyToOne(cascade = ALL, fetch = LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public NoticeAttachFile() {
    }

    @Builder
    public NoticeAttachFile(String filePath, Notice notice) {
        this.filePath = filePath;
        this.notice = notice;
    }
}
