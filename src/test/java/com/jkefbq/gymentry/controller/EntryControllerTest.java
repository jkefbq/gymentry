package com.jkefbq.gymentry.controller;

import com.jkefbq.gymentry.database.dto.PartialUserDto;
import com.jkefbq.gymentry.database.service.SubscriptionManager;
import com.jkefbq.gymentry.database.service.UserServiceImpl;
import com.jkefbq.gymentry.facade.GymEntryFacade;
import com.jkefbq.gymentry.security.JwtFilter;
import com.jkefbq.gymentry.security.MyUserDetailsService;
import com.jkefbq.gymentry.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class})
public class EntryControllerTest {

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
    GymEntryFacade gymEntryFacade;
    @MockitoBean
    private SubscriptionManager subscriptionService;

    public PartialUserDto getPartialUser() {
        return PartialUserDto.builder()
                .id(UUID.randomUUID())
                .email("email")
                .firstName("firstname")
                .build();
    }

    @BeforeEach
    public void setUp() throws ServletException, IOException {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void confirmEntryTest() throws Exception {
        mockMvc.perform(put("/user/entry"))
                .andExpect(status().isOk());
        verify(gymEntryFacade).tryEntry(any());
    }

    @Test
    @WithMockUser
    public void getUserInfoTest() throws Exception {
        PartialUserDto user = getPartialUser();
        when(userService.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @WithMockUser
    public void getActiveSubscriptionTest() throws Exception {
        when(userService.getUserIdByEmail(any())).thenReturn(Optional.of(UUID.randomUUID()));
        mockMvc.perform(get("/user/subscriptions/active"))
                .andExpect(status().isOk());
        verify(subscriptionService).getActiveSubscription(any());
    }

    @Test
    @WithMockUser
    public void activateSubscriptionTest() throws Exception {
        mockMvc.perform(post("/user/subscriptions/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UUID.randomUUID()))
        ).andExpect(status().isOk());
        verify(subscriptionService).activateSubscription(any());
    }
}
