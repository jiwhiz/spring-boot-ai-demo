package com.jiwhiz.demo.auth;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jiwhiz.demo.ControllerAbstractIT;
import com.jiwhiz.demo.TestUtil;
import com.jiwhiz.demo.user.User;
import com.jiwhiz.demo.user.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link AuthController} REST controller.
 */
public class AuthControllerIT extends ControllerAbstractIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void testAuthorize() throws Exception {
        User user = new User();
        user.setEmail("user-jwt-controller@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test"));
        userRepository.saveAndFlush(user);

        AuthRequestDTO login = new AuthRequestDTO("user-jwt-controller@example.com", "test", false);
        mockMvc
            .perform(
                post("/api/v1/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString())
            .andExpect(jsonPath("$.token").isNotEmpty())
            ;
    }

    @Test
    @Transactional
    void testAuthorizeWithRememberMe() throws Exception {
        User user = new User();
        user.setEmail("user-jwt-controller-remember-me@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test"));
        userRepository.saveAndFlush(user);

        AuthRequestDTO login = new AuthRequestDTO("user-jwt-controller-remember-me@example.com", "test", true);
        mockMvc
            .perform(
                post("/api/v1/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(login))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString())
            .andExpect(jsonPath("$.token").isNotEmpty())
            ;
    }

    @Test
    void testAuthorizeFails() throws Exception {
        AuthRequestDTO login = new AuthRequestDTO( "wrong-user@local", "wrong password", false);

        mockMvc
            .perform(
                post("/api/v1/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(login))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));
    }

}
