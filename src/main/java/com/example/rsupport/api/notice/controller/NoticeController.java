package com.example.rsupport.api.notice.controller;

import com.example.rsupport.api.common.ResponseDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeUpdateRequestDTO;
import com.example.rsupport.api.notice.domain.enums.NoticeMessage;
import com.example.rsupport.api.notice.service.NoticeService;
import com.example.rsupport.exception.notice.InvalidNoticeParameterException;
import com.example.rsupport.exception.notice.NoticeCrudErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * 공지사항 컨트롤러
 *
 * @author MC Lee
 * @created 2022-01-28
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 등록 요청을 처리합니다.
     *
     * @param dto         등록할 공지사항 정보
     * @param result      정보 검증 및 결과 처리
     * @param attachFiles 추가할 첨부파일 목록
     * @return 등록이 완료된 공지사항 고유번호
     */
    @PostMapping(value = "/notice")
    public ResponseDTO<Long> registerNotice(@Valid @RequestPart(value = "data") NoticeRegisterRequestDTO dto,
                                            BindingResult result,
                                            @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles
    ) {
        if (result.hasErrors()) throw new InvalidNoticeParameterException(result, NoticeCrudErrorCode.NOTICE_CRUD_FAIL);
        if (attachFiles == null || attachFiles.size() == 0) attachFiles = Collections.emptyList();
        return new ResponseDTO<>(noticeService.registerNotice(dto, attachFiles), NoticeMessage.SUCCESS_NOTICE_REGISTER, HttpStatus.OK);
    }

    /**
     * 공지사항 내용 및 첨부파일 수정 요청을 처리합니다.
     * Multipart/form-data는 POST로만 받아들일 수 있으므로 PATCH 또는 PUT이 사용되지 않았습니다.
     *
     * @param dto         공지사항 수정 정보를 저장하는 DTO 클래스
     * @param result      정보 검증 및 결과 처리
     * @param attachFiles 변경된 첨부파일 목록
     * @return 수정이 완료된 공지사항 고유번호
     */
    @PostMapping(value = "/notice-update")
    public ResponseDTO<Long> updateNotice(@Valid @RequestPart(value = "data") NoticeUpdateRequestDTO dto,
                                          BindingResult result,
                                          @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles
    ) {
        if (result.hasErrors()) throw new InvalidNoticeParameterException(result, NoticeCrudErrorCode.NOTICE_CRUD_FAIL);
        if (attachFiles == null || attachFiles.size() == 0) attachFiles = Collections.emptyList();
        return new ResponseDTO<>(noticeService.updateNotice(dto, attachFiles), NoticeMessage.SUCCESS_NOTICE_UPDATE, HttpStatus.OK);
    }

    /**
     * 공지사항 삭제 요청을 처리합니다.
     *
     * @param noticeId 삭제할 공지사항 번호
     */
    @DeleteMapping("/notice/{noticeId}")
    public ResponseDTO<Void> removeNotice(@PathVariable Long noticeId) {
        noticeService.removeNotice(noticeId);
        return new ResponseDTO<>(NoticeMessage.SUCCESS_NOTICE_DELETE, HttpStatus.OK);
    }

    /**
     * 공지사항 단건 조회 요청을 처리합니다.
     *
     * @param noticeId 조회할 공지사항 번호
     * @return 공지사항 정보
     */
    @GetMapping("/notice/{noticeId}")
    public ResponseDTO<NoticeDTO> selectNoticeOne(@PathVariable("noticeId") Long noticeId) {
        return new ResponseDTO<>(noticeService.selectNoticeOne(noticeId), NoticeMessage.SUCCESS_NOTICE_SELECTONE, HttpStatus.OK);
    }

}