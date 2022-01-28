package com.example.rsupport.api.notice.repository;

import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeAttachFileQueryDSLRepository {
    List<NoticeAttachFile> findByNoticeId(Long noticeId);
}
