package com.jkefbq.gymentry.controller;

import com.jkefbq.gymentry.dto.auth.EmailDto;
import com.jkefbq.gymentry.dto.for_entity.NotVerifiedUserDto;
import com.jkefbq.gymentry.dto.auth.RefreshTokenDto;
import com.jkefbq.gymentry.service.database.UserServiceImpl;
import com.jkefbq.gymentry.facade.UserAuthFacade;
import com.jkefbq.gymentry.security.JwtFilter;
import com.jkefbq.gymentry.security.MyUserDetailsService;
import com.jkefbq.gymentry.security.SecurityConfig;
import com.jkefbq.gymentry.dto.auth.UserCredentialsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class})
public class AuthControllerTest {

    private static final String EMAIL = "email@gmail.com";
    private static final String FIRSTNAME = "firstname";
    private static final String PASSWORD = "password";
    private static final String CODE = "123456";

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext context;

    @MockitoBean
    JwtFilter jwtFilter;
    @MockitoBean
    UserServiceImpl userService;
    @MockitoBean
    MyUserDetailsService myUserDetailsService;
    @MockitoBean
    private UserAuthFacade userAuthFacade;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public NotVerifiedUserDto getNotVerifiedUser() {
        return new NotVerifiedUserDto(
                UUID.randomUUID(),
                FIRSTNAME, EMAIL, PASSWORD
        );
    }

    public UserCredentialsDto getUserCredentials() {
        return new UserCredentialsDto(EMAIL, PASSWORD);
    }

    public RefreshTokenDto getRefreshTokenDto() {
        return new RefreshTokenDto("refresh.token");
    }

    @Test
    public void registerTest() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getNotVerifiedUser()))
        ).andExpect(status().isCreated());
        verify(userAuthFacade).register(any());
    }

    @Test
    public void resendActivationCodeTest() throws Exception {
        mockMvc.perform(post("/resend-activation-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EmailDto(EMAIL)))
        ).andExpect(status().isOk());
        verify(userAuthFacade).resendActivationCode(EMAIL);
    }

    @Test
    public void activateTest() throws Exception {
        mockMvc.perform(get("/activate/{}/{}", EMAIL, CODE))
                .andExpect(status().isOk());
        verify(userAuthFacade).activate(any(), any());
    }

    @Test
    public void loginTest() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getUserCredentials()))
        ).andExpect(status().isOk());
        verify(userAuthFacade).login(any());
    }

    @Test
    public void refreshTest() throws Exception {
        mockMvc.perform(post("/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRefreshTokenDto()))
        ).andExpect(status().isOk());
        verify(userAuthFacade).refresh(any());
    }

}
