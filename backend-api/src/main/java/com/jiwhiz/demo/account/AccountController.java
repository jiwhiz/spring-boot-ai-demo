package com.jiwhiz.demo.account;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jiwhiz.demo.common.Constants;
import com.jiwhiz.demo.external.MailService;
import com.jiwhiz.demo.user.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing user account.
 */
@RestController
@RequestMapping(Constants.API_ENDPOINT_BASE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account service", description = "AI Chatbot Demo API for managing user account.")
public class AccountController {

    private final AccountService accountService;

    private final MailService mailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a new user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data supplied.")
    })
    public void registerAccount(
        @Parameter(description = "New user profile.") @Valid @RequestBody RegistrationDTO registrationDTO
    ) {
        log.debug("Register a new user {}", registrationDTO);
        User user = accountService.registerUser(registrationDTO);
        mailService.sendActivationEmail(user);
    }

    @GetMapping("/activate")
    @Operation(
            summary = "Activate the newly registered user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User activated."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid activation key supplied.")
    })
    public void activateAccount(
        @Parameter(description = "Activation key.", required = true) @RequestParam(value = "key") String key
    ) {
        Optional<User> user = accountService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key: " + key);
        }
    }

}
