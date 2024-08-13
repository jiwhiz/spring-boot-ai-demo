package com.jiwhiz.demo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jiwhiz.demo.user.Authority;
import com.jiwhiz.demo.user.User;
import com.jiwhiz.demo.user.UserRepository;

@Component("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class DemoUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);

        return userRepository
            .findOneWithAuthoritiesByEmailIgnoreCase(login)
            .map(user -> createSpringSecurityUser(login, user))
            .orElseThrow(() -> {
                log.debug("User with email {} was not found in the database", login);
                throw new UsernameNotFoundException("User with email " + login + " was not found in the database");
            });
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String login, User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User " + login + " was not activated");
        }
        log.debug("Load user for login {} with authorities {}.", login, user.getAuthorities());

        List<GrantedAuthority> grantedAuthorities = user
            .getAuthorities()
            .stream()
            .map(Authority::getName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }
}
