package com.jiwhiz.demo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class DemoUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("DemoUserDetailService.loadUserByUsername({})", login);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if ("user".equals(login)) {
            return new User(login, passwordEncoder.encode("user"),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        if ("admin".equals(login)) {
            return new User(login, passwordEncoder.encode("admin"),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")));
        }

        throw new UsernameNotFoundException("User " + login + " not in the system");
    }
}
