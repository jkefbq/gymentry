package com.jkefbq.gymentry.controller;

import com.jkefbq.gymentry.dto.auth.EntryCode;
import com.jkefbq.gymentry.dto.for_entity.GymInfoDto;
import com.jkefbq.gymentry.dto.for_entity.TariffDto;
import com.jkefbq.gymentry.service.database.GymInfoService;
import com.jkefbq.gymentry.service.database.TariffService;
import com.jkefbq.gymentry.service.database.UserServiceImpl;
import com.jkefbq.gymentry.facade.AdminStatisticsFacade;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class})
public class AdminControllerTest {

    private static final String GYM_ADDRESS_PARAM = "gymAddress";
    private static final String STATISTICS_FROM_PARAM = "from";
    private static final String STATISTICS_TO_PARAM = "to";
    private static final String GYM_ADDRESS = "г.Рандомный ул.адрес д.5";
    private static final EntryCode ENTRY_CODE = new EntryCode("123456");

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
    TariffService tariffService;
    @MockitoBean
    AdminStatisticsFacade adminStatisticsFacade;
    @MockitoBean
    GymInfoService gymInfoService;
    @MockitoBean
    MyUserDetailsService myUserDetailsService;
    @MockitoBean
    GymEntryFacade gymEntryFacade;

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
    @WithMockUser(roles = "ADMIN")
    public void confirmEntryTest_admin() throws Exception {
        mockMvc.perform(post("/admin/confirm-entry")
                .contentType(MediaType.APPLICATION_JSON)
                .param(GYM_ADDRESS_PARAM, GYM_ADDRESS)
                .content(objectMapper.writeValueAsString(ENTRY_CODE))
        ).andExpect(status().isOk());
        verify(gymEntryFacade).confirmEntry(eq(ENTRY_CODE.getCode()), any(), eq(GYM_ADDRESS));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void confirmEntryTest_user() throws Exception {
        mockMvc.perform(post("/admin/confirm-entry")
                .contentType(MediaType.APPLICATION_JSON)
                .param(GYM_ADDRESS_PARAM, GYM_ADDRESS)
                .content(objectMapper.writeValueAsString(ENTRY_CODE))
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createTariffTest_admin() throws Exception {
        mockMvc.perform(post("/admin/create/tariff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TariffDto()))
        ).andExpect(status().isOk());
        verify(tariffService).create(any());
        verify(tariffService).getAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void editTariffsTest_admin() throws Exception {
        mockMvc.perform(put("/admin/edit/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(new TariffDto())))
        ).andExpect(status().isOk());
        verify(tariffService).saveAll(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllTariffTypesTest_admin() throws Exception {
        mockMvc.perform(get("/admin/tariffs/types"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteTariffsTest_admin() throws Exception {
        mockMvc.perform(delete("/admin/delete/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(new TariffDto())))
        ).andExpect(status().isOk());
        verify(tariffService).deleteAll(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void editGymInfoTest_admin() throws Exception {
        mockMvc.perform(put("/admin/edit/gym-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GymInfoDto()))
        ).andExpect(status().isOk());
        verify(gymInfoService).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllAddressesTest_admin() throws Exception {
        mockMvc.perform(get("/admin/gym/addresses")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        verify(gymInfoService).getAllAddresses();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getVisitStatisticsForPeriodTest_admin() throws Exception {
        mockMvc.perform(get("/admin/statistics/visits")
                .param(STATISTICS_FROM_PARAM, LocalDateTime.now().toString())
                .param(STATISTICS_TO_PARAM, LocalDateTime.now().toString())
                .param(GYM_ADDRESS_PARAM, GYM_ADDRESS)
        ).andExpect(status().isOk());
        verify(adminStatisticsFacade).getVisitStatisticsForPeriod(any(), any(), eq(GYM_ADDRESS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getPurchaseStatisticsForPeriodTest_admin() throws Exception {
        mockMvc.perform(get("/admin/statistics/purchases")
                .param(STATISTICS_FROM_PARAM, LocalDate.now().toString())
                .param(STATISTICS_TO_PARAM, LocalDate.now().toString())
        ).andExpect(status().isOk());
        verify(adminStatisticsFacade).getPurchaseStatisticsForPeriod(any(), any());
    }
}