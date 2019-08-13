package com.intive.atm.exeptions;

import org.springframework.http.HttpStatus;

public class AtmException extends RuntimeException {

    private HttpStatus status;
    private String code;
    private String message;

    public AtmException(String message, AtmErrorCode errorCode, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = errorCode.code();
        this.status = errorCode.httpStatus();
    }

    public AtmException(String message, AtmErrorCode errorCode) {
        super(message);
        this.message = message;
        this.code = errorCode.code();
        this.status = errorCode.httpStatus();
    }
}
