package com.jiwhiz.demo.account;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.jiwhiz.demo.TestUtil;
import com.jiwhiz.demo.common.Constants;
import com.jiwhiz.demo.user.User;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for POST /api/v1/reset-password/init and POST /api/v1/reset-password/finish
 */
public class AccountControllerResetPasswordIT extends AccountControllerAbstractIT {

    private static final String RESET_PASSWORD_API_END_POINT = Constants.API_ENDPOINT_BASE + "/reset-password";

    @Test
    @Transactional
    void testRequestPasswordReset() throws Exception {
        User user = User.builder()
                .password(RandomStringUtils.randomAlphanumeric(60))
                .activated(true)
                .email("password-reset@example.com")
                .build();
        userRepository.saveAndFlush(user);

        ResetPasswordRequestDTO requestDto = new ResetPasswordRequestDTO("password-reset@example.com");
        restAccountMockMvc
            .perform(
                post(RESET_PASSWORD_API_END_POINT + "/init")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(requestDto))
            )
            .andExpect(status().isOk())
            ;
    }

    @Test
    @Transactional
    void testRequestPasswordResetUpperCaseEmail() throws Exception {
        User user = User.builder()
                .password(RandomStringUtils.randomAlphanumeric(60))
                .activated(true)
                .email("password-reset-upper-case@example.com")
                .build();
        userRepository.saveAndFlush(user);

        ResetPasswordRequestDTO requestDto = new ResetPasswordRequestDTO("PASSWORD-RESET-UPPER-CASE@EXAMPLE.COM");

        restAccountMockMvc
            .perform(
                post(RESET_PASSWORD_API_END_POINT + "/init")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(requestDto))
            )
            .andExpect(status().isOk())
            ;
    }

    @Test
    void testRequestPasswordResetWrongEmail() throws Exception {
        ResetPasswordRequestDTO requestDto = new ResetPasswordRequestDTO("password-reset-wrong-email@example.com");
        restAccountMockMvc
            .perform(
                post(RESET_PASSWORD_API_END_POINT + "/init")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(requestDto))
            )
            .andExpect(status().isOk())
            ;
    }

    @Test
    @Transactional
    void testFinishPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setEmail("finish-password-reset@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key");
        userRepository.saveAndFlush(user);

        ResetPasswordDTO keyAndPassword = new ResetPasswordDTO(user.getResetKey(), "new password");

        restAccountMockMvc
            .perform(
                post(RESET_PASSWORD_API_END_POINT + "/finish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(keyAndPassword))
            )
            .andExpect(status().isOk())
            ;

        User updatedUser = userRepository.findOneByEmailIgnoreCase(user.getEmail()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.newPassword(), updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    void testFinishPasswordResetTooSmall() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setEmail("finish-password-reset-too-small@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key too small");
        userRepository.saveAndFlush(user);

        ResetPasswordDTO keyAndPassword = new ResetPasswordDTO(user.getResetKey(), "foo");

        restAccountMockMvc
            .perform(
                post(RESET_PASSWORD_API_END_POINT + "/finish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(keyAndPassword))
            )
            .andExpect(status().isBadRequest())
            ;

        User updatedUser = userRepository.findOneByEmailIgnoreCase(user.getEmail()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.newPassword(), updatedUser.getPassword())).isFalse();
    }

    @Test
    @Transactional
    void testFinishPasswordResetWrongKey() throws Exception {
        ResetPasswordDTO keyAndPassword = new ResetPasswordDTO("wrong reset key", "new password");

        restAccountMockMvc
            .perform(
                post(RESET_PASSWORD_API_END_POINT + "/finish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(keyAndPassword))
            )
            .andExpect(status().isBadRequest())
            ;
    }

}
