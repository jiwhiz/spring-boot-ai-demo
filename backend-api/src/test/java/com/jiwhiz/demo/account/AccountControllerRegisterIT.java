package com.jiwhiz.demo.account;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.jiwhiz.demo.TestUtil;
import com.jiwhiz.demo.common.Constants;
import com.jiwhiz.demo.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for POST /api/v1/register
 */
public class AccountControllerRegisterIT extends AccountControllerAbstractIT {

    private static final String REGISTER_API_END_POINT = Constants.API_ENDPOINT_BASE + "/register";

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
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(registrationDTO))
            )
            .andExpect(status().isCreated())
            ;

        assertThat(userRepository.findOneByEmailIgnoreCase("test-register-valid@example.com")).isPresent();
    }

    @Test
    @Transactional
    void testRegisterInvalidEmail() throws Exception {
        RegistrationDTO registrationDTO =
            RegistrationDTO.builder()
                .email("invalid") // <-- invalid
                .password("password")
                .firstName("Bob")
                .lastName("Test")
                .build();

        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(registrationDTO)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("invalid");
        assertThat(user).isEmpty();
    }

    @Test
    @Transactional
    void testRegisterInvalidPassword() throws Exception {
        RegistrationDTO registrationDTO =
            RegistrationDTO.builder()
                .email("bob@example.com")
                .password("123") // password with only 3 digits
                .firstName("Bob")
                .lastName("Test")
                .build();

        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(registrationDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"))
        ;

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("bob@example.com");
        assertThat(user).isEmpty();
    }

    @Test
    @Transactional
    void testRegisterNullPassword() throws Exception {
        RegistrationDTO registrationDTO =
            RegistrationDTO.builder()
                .email("bob@example.com")
                .password(null) // invalid null password
                .firstName("Bob")
                .lastName("Test")
                .build();

        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(registrationDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            ;

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("bob@example.com");
        assertThat(user).isEmpty();
    }

    @Test
    @Transactional
    void testRegisterEmptyPassword() throws Exception {
        RegistrationDTO registrationDTO =
            RegistrationDTO.builder()
                .email("bob@example.com")
                .password("") // invalid blank password
                .firstName("Bob")
                .lastName("Test")
                .build();

        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(registrationDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            ;

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("bob@example.com");
        assertThat(user).isEmpty();
    }

    @Test
    @Transactional
    void testRegisterWithDuplicateEmail() throws Exception {
        RegistrationDTO firstDTO =
            RegistrationDTO.builder()
                .email("test-register-duplicate-email@example.com")
                .password("password")
                .firstName("Alice")
                .lastName("Test")
                .build();

        // Register first user, should succeed
        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(firstDTO)))
            .andExpect(status().isCreated())
            ;

        Optional<User> testUser1 = userRepository.findOneByEmailIgnoreCase("test-register-duplicate-email@example.com");
        assertThat(testUser1).isPresent();

        // Duplicate email
        RegistrationDTO secondDTO =
            RegistrationDTO.builder()
                .email(firstDTO.email())
                .password(firstDTO.password())
                .firstName("Anne")
                .lastName(firstDTO.lastName())
                .build();

        // Register second (non activated) user
        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(secondDTO)))
            .andExpect(status().isCreated())
            ;

        Optional<User> testUser2 = userRepository.findOneByEmailIgnoreCase("test-register-duplicate-email@example.com");
        assertThat(testUser2).isPresent();
        assertThat(testUser2.get().getFirstName()).isEqualTo("Anne");

        // Duplicate email - with uppercase email address
        RegistrationDTO thirdDTO =
            RegistrationDTO.builder()
                .email("TEST-register-duplicate-email@example.com")
                .password(firstDTO.password())
                .firstName(firstDTO.firstName())
                .lastName(firstDTO.lastName())
                .build();

        // Register third (not activated) user
        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(thirdDTO))
            )
            .andExpect(status().isCreated())
            ;

        Optional<User> testUser3 = userRepository.findOneByEmailIgnoreCase("TEST-register-duplicate-email@example.com");
        assertThat(testUser3).isPresent();
        assertThat(testUser3.get().getEmail()).isEqualTo("test-register-duplicate-email@example.com");

        testUser3.get().setActivated(true);
        userRepository.saveAndFlush(testUser3.get());

        // Register 4th (already activated) user
        restAccountMockMvc
            .perform(
                post(REGISTER_API_END_POINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(secondDTO)))
            .andExpect(status().is4xxClientError())
            ;
    }


}
