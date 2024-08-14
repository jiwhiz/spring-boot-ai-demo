package com.jiwhiz.demo.account;

import com.jiwhiz.demo.common.exception.BusinessException;

public class AccountResourceException extends BusinessException {

    public AccountResourceException(String message) {
        super(message);
    }
}
