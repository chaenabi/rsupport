package com.example.rsupport.api.notice.repository;

import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeAttachFileRepository extends JpaRepository<NoticeAttachFile, Long>, NoticeAttachFileQueryDSLRepository {

}
