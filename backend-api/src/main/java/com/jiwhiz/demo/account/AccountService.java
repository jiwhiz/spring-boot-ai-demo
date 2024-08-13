package com.jiwhiz.demo.account;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.jiwhiz.demo.common.utils.RandomUtil;
import com.jiwhiz.demo.security.AuthoritiesConstants;
import com.jiwhiz.demo.user.Authority;
import com.jiwhiz.demo.user.AuthorityRepository;
import com.jiwhiz.demo.user.User;
import com.jiwhiz.demo.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    @Transactional
    public User registerUser(RegistrationDTO registrationDTO) {
        if (isPasswordLengthInvalid(registrationDTO.password())) {
            throw new InvalidPasswordException();
        }

        userRepository
                .findOneByEmailIgnoreCase(registrationDTO.email())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(registrationDTO.password());
        newUser.setEmail(registrationDTO.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(registrationDTO.firstName());
        newUser.setLastName(registrationDTO.lastName());

        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created new record for User: {}", newUser);
        return newUser;
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
                !StringUtils.hasText(password) ||
                        password.length() < PASSWORD_MIN_LENGTH ||
                        password.length() > PASSWORD_MAX_LENGTH
        );
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }
}
