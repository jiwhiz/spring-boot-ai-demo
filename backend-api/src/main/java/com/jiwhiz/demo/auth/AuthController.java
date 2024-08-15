package com.jiwhiz.demo.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jiwhiz.demo.common.Constants;
import com.jiwhiz.demo.security.JWTTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Constants.API_ENDPOINT_BASE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User login service", description = "Authentication by Spring Security.")
public class AuthController {

    private final JWTTokenService jwtTokenService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    @Operation(
            summary = "Login with email and password.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return JWT token.",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class)
                    )}),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid login")
    })
    public ResponseEntity<AuthResponseDTO> login(
        @Parameter(description = "Login credentials.") @Valid @RequestBody AuthRequestDTO authReqDto
    ) {
        log.info("Login request received for user: {}", authReqDto.username());
        Authentication authenticate = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authReqDto.username(), authReqDto.password()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtTokenService.createToken(authenticate, authReqDto.rememberMe());
        log.info("Return JWT token: {}", token);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
