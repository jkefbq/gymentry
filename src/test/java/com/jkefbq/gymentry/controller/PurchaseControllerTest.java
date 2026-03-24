package com.jkefbq.gymentry.controller;

import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.service.database.SubscriptionServiceImpl;
import com.jkefbq.gymentry.service.database.TariffService;
import com.jkefbq.gymentry.service.database.UserServiceImpl;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionRequestDto;
import com.jkefbq.gymentry.security.JwtFilter;
import com.jkefbq.gymentry.security.MyUserDetailsService;
import com.jkefbq.gymentry.security.SecurityConfig;
import com.jkefbq.gymentry.service.SubscriptionPriceCalculator;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
@Import({SecurityConfig.class})
public class PurchaseControllerTest {

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
    TariffService tariffService;
    @MockitoBean
    SubscriptionPriceCalculator subscriptionPriceCalculator;
    @MockitoBean
    SubscriptionServiceImpl subscriptionServiceImpl;

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
    public void getAllTariffsTest() throws Exception {
        mockMvc.perform(get("/market"))
                .andExpect(status().isOk());
        verify(tariffService).getAll();
    }

    @Test
    @WithMockUser
    public void calculatePriceTest() throws Exception {
        var visitsCount = 12;
        mockMvc.perform(get("/market/calculate-price/{tariff-type}/{visits}", TariffType.BASIC, visitsCount))
                .andExpect(status().isOk());
        verify(subscriptionPriceCalculator).calculate(TariffType.BASIC, visitsCount);
    }

    @Test
    @WithMockUser
    public void createTest() throws Exception {
        mockMvc.perform(post("/market/subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubscriptionRequestDto())))
                .andExpect(status().isCreated());
        verify(subscriptionServiceImpl).sendCreateMessage(any(), any());
    }

}
