package com.jiwhiz.demo.account;

import com.jiwhiz.demo.common.exception.BusinessException;

public class InvalidPasswordException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super("Incorrect password");
    }
}
