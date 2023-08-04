package com.wanted.preonboarding.global.exception;

public class BusinessLoginException extends RuntimeException{

    public BusinessLoginException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMsg());
    }

    public BusinessLoginException(String msg){
        super(msg);
    }
}
