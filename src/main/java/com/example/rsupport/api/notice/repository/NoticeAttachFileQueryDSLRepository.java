package com.example.rsupport.api.notice.repository;

import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공지사항 첨부파일 레포지토리 - QueryDSL
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Repository
public interface NoticeAttachFileQueryDSLRepository {
    List<NoticeAttachFile> findByNoticeId(Long noticeId);
}
