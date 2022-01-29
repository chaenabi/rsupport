package com.example.rsupport.api.notice.service;

import com.example.rsupport.api.notice.domain.dto.NoticeDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeUpdateRequestDTO;
import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import com.example.rsupport.api.notice.repository.NoticeAttachFileRepository;
import com.example.rsupport.api.notice.repository.NoticeRepository;
import com.example.rsupport.api.notice.utils.AttachFileManager;
import com.example.rsupport.exception.common.BizException;
import com.example.rsupport.exception.notice.NoticeCrudErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

/**
 * 공지사항 비즈니스 로직 서비스 객체
 *
 * @author MC Lee
 * @created 2022-01-29
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {EmptyResultDataAccessException.class, SQLException.class, BizException.class})
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeAttachFileRepository noticeAttachFileRepository;
    private final AttachFileManager attachFileManager;

    /**
     * 공지사항 등록 요청 처리
     *
     * @param dto         공지사항 등록 요청 정보를 담고 있는 객체
     * @param attachFiles 첨부파일 목록
     * @return 등록이 완료된 공지사항의 고유 번호
     */
    public Long registerNotice(NoticeRegisterRequestDTO dto, List<MultipartFile> attachFiles) {
        Notice savedNotice = noticeRepository.save(dto.toEntity());
        List<NoticeAttachFile> files = attachFileManager.saveUploadFilesToDisk(attachFiles, savedNotice);
        noticeAttachFileRepository.saveAll(files);

        return savedNotice.getId();
    }

    /**
     * 공지사항 수정 요청 처리
     *
     * @param dto         공지사항 수정 요청 정보를 담고 있는 객체
     * @param attachFiles 첨부파일 목록
     * @return 수정이 완료된 공지사항의 고유 번호
     */
    public Long updateNotice(NoticeUpdateRequestDTO dto, List<MultipartFile> attachFiles) {
        Notice wantToUpdateNotice = noticeRepository.findById(dto.getNoticeId())
                .orElseThrow(() -> new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND));

        wantToUpdateNotice.setTitle(dto.getTitle());
        wantToUpdateNotice.setContent(dto.getContent());

        List<NoticeAttachFile> files = attachFileManager.saveUploadFilesToDisk(attachFiles, wantToUpdateNotice);
        List<NoticeAttachFile> savedAttachFiles = noticeAttachFileRepository.findByNoticeId(wantToUpdateNotice.getId());
        noticeAttachFileRepository.deleteAll(savedAttachFiles);
        noticeAttachFileRepository.flush();
        noticeAttachFileRepository.saveAll(files);

        return wantToUpdateNotice.getId();
    }

    /**
     * 공지사항 삭제 요청 처리
     *
     * @param noticeId 삭제 요청된 공지사항의 고유 번호
     */
    public void removeNotice(Long noticeId) {
        noticeRepository
                .findById(noticeId)
                .orElseThrow(() -> new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND));

        noticeRepository.deleteById(noticeId);
    }

    /**
     * 공지사항 단건 조회 요청 처리
     *
     * @param noticeId 삭제 요청된 공지사항의 고유 번호
     * @return 단건 조회 데이터가 담긴 도메인 객체
     */
    public NoticeDTO selectNoticeOne(Long noticeId) {
        Notice findNotice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND));
        findNotice.setSawCount(findNotice.getSawCount() + 1);
        return NoticeDTO.builder()
                .noticeId(findNotice.getId())
                .title(findNotice.getTitle())
                .content(findNotice.getContent())
                .startTime(findNotice.getStartTime())
                .endTime(findNotice.getEndTime())
                .sawCount(findNotice.getSawCount())
                .build();
    }
}
