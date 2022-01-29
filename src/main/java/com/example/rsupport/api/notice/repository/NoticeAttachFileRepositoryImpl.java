package com.example.rsupport.api.notice.repository;

import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import com.example.rsupport.api.notice.domain.entity.QNoticeAttachFile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 공지사항 첨부파일 레포지토리 구현클래스.
 * 쿼리 DSL을 기본 API로 사용합니다.
 *
 * @author MC Lee
 * @created 2022-01-26
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Repository
public class NoticeAttachFileRepositoryImpl implements NoticeAttachFileQueryDSLRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    @Override
    public List<NoticeAttachFile> findByNoticeId(Long noticeId) {
        queryFactory = new JPAQueryFactory(entityManager);
        final QNoticeAttachFile qAttachFile = QNoticeAttachFile.noticeAttachFile;
        return queryFactory.selectFrom(qAttachFile)
                .where(qAttachFile.notice.id.eq(noticeId))
                .fetch();
    }
}
