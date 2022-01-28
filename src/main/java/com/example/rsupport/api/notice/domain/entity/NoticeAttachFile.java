package com.example.rsupport.api.notice.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;
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

    private String filename;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public NoticeAttachFile() {
    }

    @Builder
    public NoticeAttachFile(String filename, Notice notice) {
        this.filename = filename;
        this.notice = notice;
    }
}
