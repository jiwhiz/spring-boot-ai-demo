package com.jiwhiz.demo.common.rest;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jiwhiz.demo.common.exception.BusinessException;

import jakarta.persistence.OptimisticLockException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleRuntimeError(
            final RuntimeException ex, final WebRequest request) {
        return handleExceptionInternal(ex, null,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusinessError(
            final BusinessException ex, final WebRequest request) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .title("Business Validation Failed")
                .type(URI.create("https://api.jiwhiz.com/errors/validation"))
                .property("errorCategory", "Business")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLockException(
            final OptimisticLockException ex, final WebRequest request) {
        return ErrorResponse.builder(ex, HttpStatus.CONFLICT, ex.getMessage())
                .title("Optimistic Locking Error")
                .type(URI.create("https://api.jiwhiz.com/errors/optimisticlocking"))
                .property("errorCategory", "Database")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(
            final AuthenticationException ex, final WebRequest request) {
        return ErrorResponse.builder(ex, HttpStatus.UNAUTHORIZED, ex.getMessage())
                .title("Login Failed")
                .detail("You entered wrong username or password, please try again.")
                .type(URI.create("https://api.jiwhiz.com/errors/unauthorized"))
                .property("errorCategory", "Security")
                .property("timestamp", Instant.now())
                .build();
    }

}
