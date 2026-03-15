package com.jkefbq.gymentry;

import com.jkefbq.gymentry.database.dto.NotVerifiedUserDto;
import com.jkefbq.gymentry.database.dto.TariffType;
import com.jkefbq.gymentry.database.service.NotVerifiedUserService;
import com.jkefbq.gymentry.database.service.SubscriptionManager;
import com.jkefbq.gymentry.database.service.UserService;
import com.jkefbq.gymentry.dto.EntryCode;
import com.jkefbq.gymentry.dto.SubscriptionRequestDto;
import com.jkefbq.gymentry.facade.MarketFacade;
import com.jkefbq.gymentry.security.JwtService;
import com.jkefbq.gymentry.security.UserCredentialsDto;
import com.jkefbq.gymentry.service.MailService;
import com.jkefbq.gymentry.service.SubscriptionPriceCalculator;
import com.jkefbq.gymentry.service.VerificationCodeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/insert-tariffs.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Transactional
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class GymEntryApplicationTests {

    public static final String PSQL_IMG = "postgres:18-alpine";
    public static final String REDIS_IMG = "redis:7-alpine";
    public static final String USER_EMAIL = "user@gmail.com";
    public static final String ADMIN_EMAIL = "admin@gmail.com";
    public static final String USER_PASSWORD = "user-password";
    public static final String USER_NAME = "user";
    public static final String GYM_ADDRESS_PARAM = "gymAddress";
    public static final String GYM_ADDRESS = "test-address";

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webContext;
    @Autowired
    JwtService jwtService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SubscriptionPriceCalculator subscriptionPriceCalculator;
    @Autowired
    SubscriptionManager subscriptionManager;

    @MockitoBean
    MailService mailService;
    @MockitoBean
    VerificationCodeService verificationCodeService;
    @MockitoSpyBean
    NotVerifiedUserService notVerifiedUserService;
    @MockitoSpyBean
    UserService userService;
    @MockitoSpyBean
    MarketFacade marketFacade;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>(PSQL_IMG)
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(REDIS_IMG)
                    .waitingFor(Wait.forListeningPort())
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void setPostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.liquibase.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.liquibase.user", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.liquibase.password", POSTGRES_CONTAINER::getPassword);
    }

    @DynamicPropertySource
    static void setRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Order(1)
    void contextLoads() {}

    @Test
    public void registerTest() throws Exception {
        var user = NotVerifiedUserDto.builder().firstName(USER_NAME).email(USER_EMAIL).password(USER_PASSWORD).build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        ).andExpect(status().isCreated());
        verify(mailService).sendConfirmEmail(USER_EMAIL);
        verify(notVerifiedUserService).create(any());
    }

    @Test
    @Sql("/insert-test-user.sql")
    public void loginTest() throws Exception {
        var user = new UserCredentialsDto(USER_EMAIL, USER_PASSWORD);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @Sql("/insert-test-user.sql")
    @Sql("/insert-test-admin.sql")
    public void entryToGymSimulationTest_userAdminInteraction() throws Exception {
        //user
        var user = userService.findByEmail(USER_EMAIL).orElseThrow();
        var visitsLeftBefore = subscriptionManager.getActiveSubscription(user.getId()).getVisitsLeft();
        var userAccessToken = jwtService.generateAccessToken(USER_EMAIL);
        String entryCode = mockMvc.perform(put("/user/entry")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        //admin
        var adminAccessToken = jwtService.generateAccessToken(ADMIN_EMAIL);
        mockMvc.perform(post("/admin/confirm-entry")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
                .param(GYM_ADDRESS_PARAM, GYM_ADDRESS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EntryCode(entryCode)))
        ).andExpect(status().isOk());

        var visitsLeftAfter = subscriptionManager.getActiveSubscription(user.getId()).getVisitsLeft();
        assertEquals(visitsLeftBefore, visitsLeftAfter + 1);
    }

    @Test
    @Sql("/insert-test-user.sql")
    public void buySubscriptionTest() throws Exception {
        doNothing().when(marketFacade).create(any(), any());
        var visitsTotal = ThreadLocalRandom.current().nextInt(12);
        var tariffType = TariffType.BASIC;
        var sub = new SubscriptionRequestDto(visitsTotal, tariffType);
        var accessToken = jwtService.generateAccessToken(USER_EMAIL);

        mockMvc.perform(post("/market/subscription")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sub))
        ).andExpect(status().isCreated());
    }


}
