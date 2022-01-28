package com.example.rsupport.api.notice.service;

import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeUpdateRequestDTO;
import com.example.rsupport.api.notice.domain.entity.Notice;
import com.example.rsupport.api.notice.domain.entity.NoticeAttachFile;
import com.example.rsupport.api.notice.repository.NoticeAttachFileRepository;
import com.example.rsupport.api.notice.repository.NoticeRepository;
import com.example.rsupport.exception.common.BizException;
import com.example.rsupport.exception.notice.NoticeCrudErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {EmptyResultDataAccessException.class, SQLException.class, BizException.class})
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeAttachFileRepository noticeAttachFileRepository;

    public Long registerNotice(NoticeRegisterRequestDTO dto, List<MultipartFile> attachFiles) {
        Notice savedNotice = noticeRepository.save(dto.toEntity());
        List<NoticeAttachFile> files = convertMulipartFileToDTO(attachFiles, savedNotice);
        noticeAttachFileRepository.saveAll(files);

        return savedNotice.getId();
    }

    public Long updateNotice(NoticeUpdateRequestDTO dto) {
        Notice wantToUpdateNotice = noticeRepository.findById(dto.getNoticeId())
                .orElseThrow(() -> new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND));

        return wantToUpdateNotice.getId();
    }

    public void removeNotice(Long noticeId) {
        noticeRepository
                .findById(noticeId)
                .orElseThrow(() -> new BizException(NoticeCrudErrorCode.NOTICE_NOT_FOUND));

        noticeRepository.deleteById(noticeId);
    }

    private List<NoticeAttachFile> convertMulipartFileToDTO(List<MultipartFile> attachFiles, Notice notice) {
        List<NoticeAttachFile> files = new ArrayList<>();

        attachFiles.forEach(file -> files.add(NoticeAttachFile.builder()
                .filePath(file.getOriginalFilename())
                .notice(notice)
                .build()));
        return files;
    }
}
