package com.example.rsupport.api.notice.repository;

import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;

import java.util.List;

public interface NoticeAttachFileQueryDSLRepository {
    List<NoticeAttachFile> findByNoticeId(Long noticeId);
}
