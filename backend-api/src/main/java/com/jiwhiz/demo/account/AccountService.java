package com.jiwhiz.demo.account;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jiwhiz.demo.common.utils.RandomUtil;
import com.jiwhiz.demo.security.AuthoritiesConstants;
import com.jiwhiz.demo.user.Authority;
import com.jiwhiz.demo.user.AuthorityRepository;
import com.jiwhiz.demo.user.User;
import com.jiwhiz.demo.user.UserRepository;

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
        log.debug("Register a new user {}", registrationDTO);

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

        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        User newUser =
            User.builder()
                .email(registrationDTO.email().toLowerCase())
                .password(passwordEncoder.encode(registrationDTO.password()))
                .firstName(registrationDTO.firstName())
                .lastName(registrationDTO.lastName())
                .activated(false) // new user is not active
                .activationKey(RandomUtil.generateActivationKey()) // new user gets activation key
                .authorities(authorities)
                .build();
        userRepository.save(newUser);
        log.debug("Created new record for User: {}", newUser.getEmail());
        return newUser;
    }

    @Transactional
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
                .findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the activation key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    @Transactional
    public Optional<User> requestPasswordReset(String mail) {
        log.debug("User request to reset password for email {}", mail);
        return userRepository
                .findOneByEmailIgnoreCase(mail)
                .filter(User::isActivated)
                .map(user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(Instant.now());
                    return user;
                });
    }

    @Transactional
    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        if (isPasswordLengthInvalid(newPassword)) {
            throw new InvalidPasswordException();
        }

        return userRepository
                .findOneByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    return user;
                });
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
