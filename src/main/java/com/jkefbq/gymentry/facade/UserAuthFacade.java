package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.NotVerifiedUserDto;
import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.UserWithPassword;
import com.jkefbq.gymentry.service.database.NotVerifiedUserService;
import com.jkefbq.gymentry.service.database.UserService;
import com.jkefbq.gymentry.exception.InvalidTokenException;
import com.jkefbq.gymentry.exception.InvalidVerificationCodeException;
import com.jkefbq.gymentry.exception.TimeoutActivationCodeException;
import com.jkefbq.gymentry.exception.UserAlreadyExistsException;
import com.jkefbq.gymentry.security.JwtService;
import com.jkefbq.gymentry.dto.auth.TokenPairDto;
import com.jkefbq.gymentry.dto.auth.UserCredentialsDto;
import com.jkefbq.gymentry.security.UserRole;
import com.jkefbq.gymentry.service.MailService;
import com.jkefbq.gymentry.service.VerificationCodeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class UserAuthFacade {

    private static final ExecutorService es = Executors.newCachedThreadPool();
    private final NotVerifiedUserService notVerifiedUserService;
    private final UserService userService;
    private final MailService mailService;
    private final JwtService jwtService;
    private final VerificationCodeService verificationCodeService;

    @RateLimiter(name = "register")
    @Transactional
    public void register(NotVerifiedUserDto user) {
        boolean existsTmp = notVerifiedUserService.existsByEmail(user.getEmail());
        boolean existsCommon = userService.existsByEmail(user.getEmail());
        if (existsTmp || existsCommon) {
            throw new UserAlreadyExistsException("user with email '" + user.getEmail() + "' already exists");
        }
        notVerifiedUserService.create(user);
        es.execute(() -> mailService.sendConfirmEmail(user.getEmail()));
    }

    @RateLimiter(name = "login")
    @Transactional
    public TokenPairDto login(UserCredentialsDto userCredentials) throws AuthenticationException {
        boolean isCorrectData = userService.isCorrectEmailAndPassword(userCredentials.getEmail(), userCredentials.getPassword());
        if (!isCorrectData) {
            throw new AuthenticationException("incorrect login or password");
        }
        return jwtService.generateTokenPair(userCredentials.getEmail());
    }

    @RateLimiter(name = "activate-user")
    @Transactional
    public TokenPairDto activate(String email, String code) throws InvalidVerificationCodeException, TimeoutActivationCodeException {
        if (!verificationCodeService.compareVerificationCode(email, code)) {
            throw new InvalidVerificationCodeException("incorrect verification code");
        }
        notVerifiedUserService.findUserByEmail(email).ifPresentOrElse(
                this::deleteTmpUserAndCreateCommonUser,
                () -> {
                    throw new IllegalStateException("non-verified user with email '" + email + "' not found");
                });
        return jwtService.generateTokenPair(email);
    }

    @RateLimiter(name = "refresh")
    @Transactional
    public TokenPairDto refresh(String refreshToken) throws InvalidTokenException {
        if (jwtService.isAnyTokenValid(refreshToken)) {
            String email = jwtService.getEmailFromToken(refreshToken);
            PartialUserDto authenticUser = userService.findByEmail(email).orElseThrow(NoSuchElementException::new);
            return jwtService.refreshAccessTokenAndRotate(authenticUser.getEmail());
        }
        throw new InvalidTokenException("Invalid refresh token");
    }

    @RateLimiter(name = "send-activation-code")
    public String resendActivationCode(String email) {
        verificationCodeService.deleteVerificationCode(email);
        return verificationCodeService.generateAndSaveVerificationCode(email);
    }

    @Transactional
    public void deleteTmpUserAndCreateCommonUser(NotVerifiedUserDto notVerifiedUser) {
        var verifiedUser = UserWithPassword.builder()
                .firstName(notVerifiedUser.getFirstName())
                .password(notVerifiedUser.getPassword())
                .email(notVerifiedUser.getEmail())
                .role(UserRole.USER)
                .build();
        userService.create(verifiedUser);
        notVerifiedUserService.deleteByEmail(notVerifiedUser.getEmail());
    }
}
