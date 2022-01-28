package com.example.rsupport.api.notice.repository;

import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import com.example.rsupport.api.notice.domain.entity.QNoticeAttachFile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
