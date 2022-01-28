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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {EmptyResultDataAccessException.class, SQLException.class, BizException.class})
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeAttachFileRepository noticeAttachFileRepository;
    private final AttachFileManager attachFileManager;

    public Long registerNotice(NoticeRegisterRequestDTO dto, List<MultipartFile> attachFiles) {
        Notice savedNotice = noticeRepository.save(dto.toEntity());
        List<NoticeAttachFile> files = attachFileManager.saveUploadFilesToDisk(attachFiles, savedNotice);
        noticeAttachFileRepository.saveAll(files);

        return savedNotice.getId();
    }

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

    public void removeNotice(Long noticeId) {
        noticeRepository
                .findById(noticeId)
                .orElseThrow(() -> new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND));

        noticeRepository.deleteById(noticeId);
    }

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
