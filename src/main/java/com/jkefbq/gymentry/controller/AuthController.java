package com.jkefbq.gymentry.controller;

import com.jkefbq.gymentry.dto.for_entity.NotVerifiedUserDto;
import com.jkefbq.gymentry.dto.auth.EmailDto;
import com.jkefbq.gymentry.dto.auth.RefreshTokenDto;
import com.jkefbq.gymentry.exception.InvalidTokenException;
import com.jkefbq.gymentry.exception.InvalidVerificationCodeException;
import com.jkefbq.gymentry.exception.TimeoutActivationCodeException;
import com.jkefbq.gymentry.exception.UserAlreadyExistsException;
import com.jkefbq.gymentry.facade.UserAuthFacade;
import com.jkefbq.gymentry.dto.auth.TokenPairDto;
import com.jkefbq.gymentry.dto.auth.UserCredentialsDto;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthFacade userAuthFacade;

    @PostMapping("/register")
    public ResponseEntity<@NonNull String> register(@RequestBody @Valid NotVerifiedUserDto user) throws UserAlreadyExistsException {
        log.info("call /register for user with id {}", user.getId());
        userAuthFacade.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("successful registration, confirmation code has been sent to your email");
    }

    @PostMapping("/resend-activation-code")
    public String resendActivationCode(@RequestBody EmailDto emailDto) {
        log.info("call /resend-activation-code for user with email {}", emailDto);
        return userAuthFacade.resendActivationCode(emailDto.getEmail());
    }

    @GetMapping("/activate/{email}/{code}")
    public TokenPairDto activate(@PathVariable String email, @PathVariable String code) throws TimeoutActivationCodeException, InvalidVerificationCodeException {
        log.info("call /activate/{}/{}", email, code);
        return userAuthFacade.activate(email, code);
    }

    @PostMapping("/login")
    public TokenPairDto login(@RequestBody @Valid UserCredentialsDto userCredentials) throws AuthenticationException {
        log.info("call /login for user with email {}", userCredentials.getEmail());
        return userAuthFacade.login(userCredentials);
    }


    @PostMapping("/refresh")
    public TokenPairDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws InvalidTokenException {
        log.info("call /refresh with token {}", refreshTokenDto.getRefreshToken());
        return userAuthFacade.refresh(refreshTokenDto.getRefreshToken());
    }

}