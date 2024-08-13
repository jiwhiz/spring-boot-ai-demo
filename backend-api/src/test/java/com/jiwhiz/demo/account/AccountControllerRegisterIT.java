package com.jiwhiz.demo.account;


import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.jiwhiz.demo.TestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for POST /api/v1/register
 */
public class AccountControllerRegisterIT extends AccountControllerAbstractIT {

    @Test
    @Transactional
    void testRegisterValid() throws Exception {
        RegistrationDTO registrationDTO =
            RegistrationDTO.builder()
                .email("test-register-valid@example.com")
                .password("password")
                .firstName("Alice")
                .lastName("Test")
                .build();
        assertThat(userRepository.findOneByEmailIgnoreCase("test-register-valid@example.com")).isEmpty();

        restAccountMockMvc
            .perform(
                post("/api/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(registrationDTO))
            )
            .andExpect(status().isCreated())
            ;

        assertThat(userRepository.findOneByEmailIgnoreCase("test-register-valid@example.com")).isPresent();
    }


}
