package com.example.rsupport.exception.notice;

import com.example.rsupport.exception.common.ErrorCode;
import com.example.rsupport.exception.common.InvalidParameterException;
import org.springframework.validation.Errors;

public class InvalidNoticeParameterException extends InvalidParameterException {
    public InvalidNoticeParameterException(Errors errors, ErrorCode errorCode) {
        super(errors, errorCode);
    }
}
