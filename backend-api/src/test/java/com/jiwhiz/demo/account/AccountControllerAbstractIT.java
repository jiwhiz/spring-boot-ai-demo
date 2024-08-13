package com.jiwhiz.demo.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.jiwhiz.demo.ControllerAbstractIT;
import com.jiwhiz.demo.user.AuthorityRepository;
import com.jiwhiz.demo.user.UserRepository;

public abstract class AccountControllerAbstractIT extends ControllerAbstractIT{

    static final String TEST_USER_LOGIN = "test@local";

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthorityRepository authorityRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected MockMvc restAccountMockMvc;

}
