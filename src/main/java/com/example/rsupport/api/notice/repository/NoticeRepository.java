package com.example.rsupport.api.notice.repository;

import com.example.rsupport.api.notice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 공지사항 레포지토리
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
