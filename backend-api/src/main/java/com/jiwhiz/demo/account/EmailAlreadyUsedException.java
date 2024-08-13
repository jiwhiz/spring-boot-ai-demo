package com.jiwhiz.demo.account;

import com.jiwhiz.demo.common.exception.BusinessException;

public class EmailAlreadyUsedException extends BusinessException{

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super("Email is already in use!");
    }
}
