package com.example.rsupport.api.notice.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Notice {

    @Id
    @Column(name = "notice_id")
    private Long id;
}
