package com.example.rsupport.api.notice.controller;

import com.example.rsupport.api.notice.common.ResponseDTO;
import com.example.rsupport.api.notice.domain.dto.NoticeRegisterRequestDTO;
import com.example.rsupport.api.notice.domain.enums.NoticeMessage;
import com.example.rsupport.api.notice.service.NoticeService;
import com.example.rsupport.exception.notice.InvalidNoticeParameterException;
import com.example.rsupport.exception.notice.NoticeCrudErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/notice")
    public ResponseDTO<Long> registerNotice(@Valid @RequestBody NoticeRegisterRequestDTO dto, BindingResult result) {
        if (result.hasErrors()) throw new InvalidNoticeParameterException(result, NoticeCrudErrorCode.NOTICE_CRUD_FAIL);
        return new ResponseDTO<>(noticeService.registerNotice(dto), NoticeMessage.SUCCESS_REGISTER_NOTICE, HttpStatus.OK);
    }
}