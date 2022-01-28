package com.example.rsupport.api.notice.controller;

import com.example.rsupport.api.common.ResponseDTO;
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
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(value = "/notice")
    public ResponseDTO<Long> registerNotice(@Valid @RequestPart(value = "data") NoticeRegisterRequestDTO dto,
                                            BindingResult result,
                                            @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles
    ) {
        if (result.hasErrors()) throw new InvalidNoticeParameterException(result, NoticeCrudErrorCode.NOTICE_CRUD_FAIL);
        if (attachFiles == null || attachFiles.size() == 0) attachFiles = Collections.emptyList();
        return new ResponseDTO<>(noticeService.registerNotice(dto, attachFiles), NoticeMessage.SUCCESS_NOTICE_REGISTER, HttpStatus.OK);
    }

    @PatchMapping("/notice")
    public ResponseDTO<Long> updateNotice(@Valid @RequestBody NoticeUpdateRequestDTO dto, BindingResult result) {
        if (result.hasErrors()) throw new InvalidNoticeParameterException(result, NoticeCrudErrorCode.NOTICE_CRUD_FAIL);
        return new ResponseDTO<>(noticeService.updateNotice(dto), NoticeMessage.SUCCESS_NOTICE_UPDATE, HttpStatus.OK);
    }

    @DeleteMapping("/notice/{noticeId}")
    public ResponseDTO<Void> removeNotice(@PathVariable Long noticeId) {
        noticeService.removeNotice(noticeId);
        return new ResponseDTO<>(NoticeMessage.SUCCESS_NOTICE_DELETE, HttpStatus.OK);
    }

}