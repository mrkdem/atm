package com.intive.atm.exeptions;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;

public enum AtmErrorCode {

    ERROR_CUSTOMER_NOT_FOUND("01", INTERNAL_SERVER_ERROR),
    ERROR_ACCOUNT_NOT_FOUND("02", INTERNAL_SERVER_ERROR),
    ERROR_ACCOUNT_WITHDRAWAL("03", INTERNAL_SERVER_ERROR);

    private String code;
    private HttpStatus httpStatus;

    AtmErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String code() {
        return String.format("ATM-%s", this.code);
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

}