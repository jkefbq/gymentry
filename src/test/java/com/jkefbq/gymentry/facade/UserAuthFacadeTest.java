package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.NotVerifiedUserDto;
import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.service.database.NotVerifiedUserService;
import com.jkefbq.gymentry.service.database.UserService;
import com.jkefbq.gymentry.exception.InvalidTokenException;
import com.jkefbq.gymentry.exception.InvalidVerificationCodeException;
import com.jkefbq.gymentry.exception.TimeoutActivationCodeException;
import com.jkefbq.gymentry.exception.UserAlreadyExistsException;
import com.jkefbq.gymentry.security.JwtService;
import com.jkefbq.gymentry.dto.auth.UserCredentialsDto;
import com.jkefbq.gymentry.service.MailService;
import com.jkefbq.gymentry.service.VerificationCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAuthFacadeTest {

    @Mock
    NotVerifiedUserService notVerifiedUserService;
    @Mock
    UserService userService;
    @Mock
    MailService mailService;
    @Mock
    JwtService jwtService;
    @Mock
    VerificationCodeService verificationCodeService;

    @Spy
    @InjectMocks
    UserAuthFacade userAuthFacade;

    @Test
    public void registerTest() throws UserAlreadyExistsException {
        when(notVerifiedUserService.existsByEmail(any())).thenReturn(false);
        when(userService.existsByEmail(any())).thenReturn(false);
        var user = NotVerifiedUserDto.builder().id(UUID.randomUUID()).email("email").firstName("firstName").password("password").build();

        userAuthFacade.register(user);

        await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(notVerifiedUserService).create(user);
                    verify(mailService).sendConfirmEmail(user.getEmail());
                });
    }

    @Test
    public void registerTest_assertThrowUserAlreadyExistsException() {
        when(notVerifiedUserService.existsByEmail(any())).thenReturn(true);
        when(userService.existsByEmail(any())).thenReturn(true);
        var user = NotVerifiedUserDto.builder().id(UUID.randomUUID()).email("email").firstName("firstName").password("password").build();

        assertThrows(UserAlreadyExistsException.class, () -> userAuthFacade.register(user));
    }

    @Test
    public void loginTest() throws AuthenticationException {
        when(userService.isCorrectEmailAndPassword(any(), any())).thenReturn(true);
        var user = new UserCredentialsDto("email", "password");

        userAuthFacade.login(user);

        verify(jwtService).generateTokenPair(user.getEmail());
    }

    @Test
    public void loginTest_assertThrowAuthenticationException() {
        when(userService.isCorrectEmailAndPassword(any(), any())).thenReturn(false);
        var user = new UserCredentialsDto("email", "password");

        assertThrows(AuthenticationException.class, () -> userAuthFacade.login(user));
    }

    @Test
    public void activateTest() throws TimeoutActivationCodeException, InvalidVerificationCodeException {
        var user = new NotVerifiedUserDto();
        when(verificationCodeService.compareVerificationCode(any(), any())).thenReturn(true);
        when(notVerifiedUserService.findUserByEmail(any())).thenReturn(Optional.of(user));

        userAuthFacade.activate("email", "code");

        verify(userAuthFacade).deleteTmpUserAndCreateCommonUser(user);
        verify(jwtService).generateTokenPair(any());
    }

    @Test
    public void activateTest_throwInvalidVerificationCodeException() throws TimeoutActivationCodeException {
        when(verificationCodeService.compareVerificationCode(any(), any())).thenReturn(false);

        assertThrows(InvalidVerificationCodeException.class, () -> userAuthFacade.activate("email", "code"));
    }

    @Test
    public void activateTest_throwIllegalStateException() throws TimeoutActivationCodeException {
        var user = new NotVerifiedUserDto();
        when(verificationCodeService.compareVerificationCode(any(), any())).thenReturn(true);
        when(notVerifiedUserService.findUserByEmail(any())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> userAuthFacade.activate("email", "code"));
    }

    @Test
    public void refreshTest() throws InvalidTokenException {
        when(jwtService.isAnyTokenValid(any())).thenReturn(true);
        when(userService.findByEmail(any())).thenReturn(Optional.of(new PartialUserDto()));
        var refreshToken = UUID.randomUUID().toString();

        userAuthFacade.refresh(refreshToken);

        verify(jwtService).getEmailFromToken(refreshToken);
        verify(jwtService).refreshAccessTokenAndRotate(any());
    }

    @Test
    public void refreshTest_throwInvalidTokenException() {
        when(jwtService.isAnyTokenValid(any())).thenReturn(false);
        var refreshToken = UUID.randomUUID().toString();

        assertThrows(InvalidTokenException.class, () -> userAuthFacade.refresh(refreshToken));
    }

    @Test
    public void resendActivationCodeTest() {
        var email = "email";
        userAuthFacade.resendActivationCode(email);

        verify(verificationCodeService).deleteVerificationCode(email);
        verify(verificationCodeService).generateAndSaveVerificationCode(email);
    }

    @Test
    public void deleteTmpUserAndCreateCommonUserTest() {
        var user = new NotVerifiedUserDto();

        userAuthFacade.deleteTmpUserAndCreateCommonUser(user);

        verify(userService).create(any());
        verify(notVerifiedUserService).deleteByEmail(any());
    }
}
