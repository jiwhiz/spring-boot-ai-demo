package com.jiwhiz.demo.account;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.jiwhiz.demo.common.Constants;
import com.jiwhiz.demo.common.utils.RandomUtil;
import com.jiwhiz.demo.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for GET /api/v1/activate
 */
public class AccountControllerActivateIT extends AccountControllerAbstractIT {

    private static final String ACTIVATE_API_END_POINT = Constants.API_ENDPOINT_BASE + "/activate";

    @Test
    @Transactional
    void testActivateAccount() throws Exception {
        final String activationKey = "some-activation-key";
        User user = User.builder()
            .email("activate-account@example.com")
            .password(RandomUtil.generatePassword())
            .activated(false)
            .activationKey(activationKey)
            .build();

        userRepository.saveAndFlush(user);

        restAccountMockMvc.perform(
            get(ACTIVATE_API_END_POINT).param("key", activationKey)
        ).andExpect(status().isOk());

        user = userRepository.findOneByEmailIgnoreCase(user.getEmail()).orElse(null);
        assertThat(user.isActivated()).isTrue();
    }

    @Test
    @Transactional
    void testActivateAccountWithWrongKey() throws Exception {
        restAccountMockMvc.perform(
            get(ACTIVATE_API_END_POINT).param("key", "wrong-key")
        ).andExpect(status().isBadRequest());
    }

}
