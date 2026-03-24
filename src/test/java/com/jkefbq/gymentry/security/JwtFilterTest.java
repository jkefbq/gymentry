package com.jkefbq.gymentry.security;

import com.jkefbq.gymentry.dto.for_entity.UserWithPassword;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock
    JwtService jwtService;
    @Mock
    UserDetailsService userDetailsService;

    @Mock(answer = Answers.RETURNS_MOCKS)
    HttpServletRequest request;
    @Mock(answer = Answers.RETURNS_MOCKS)
    HttpServletResponse response;
    @Mock(answer = Answers.RETURNS_MOCKS)
    FilterChain chain;

    @Spy
    @InjectMocks
    JwtFilter jwtFilter;

    @Test
    public void doFilterInternalTest_correct() throws ServletException, IOException {
        var token = UUID.randomUUID().toString();
        doReturn(token).when(jwtFilter).getAccessTokenFromRequest(request);
        doNothing().when(jwtFilter).setUserDetailsToSecurityContextHolder(any());
        when(jwtService.isAnyTokenValid(token)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, chain);

        verify(jwtFilter).setUserDetailsToSecurityContextHolder(token);
        verify(chain).doFilter(request, response);
    }

    @Test
    public void doFilterInternalTest_assertInvalid() throws ServletException, IOException {
        var token = UUID.randomUUID().toString();
        doReturn(token).when(jwtFilter).getAccessTokenFromRequest(request);
        when(jwtService.isAnyTokenValid(token)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, chain);

        verify(jwtFilter).getAccessTokenFromRequest(request);
        verify(jwtFilter, never()).setUserDetailsToSecurityContextHolder(token);
        verify(chain).doFilter(request, response);
    }

    @Test
    public void setUserDetailsToSecurityContextHolderTest() {
        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            var token = UUID.randomUUID().toString();
            when(jwtService.getEmailFromToken(token)).thenReturn("email");
            when(userDetailsService.loadUserByUsername(any())).thenReturn(new CustomUserDetails(UserWithPassword.builder().role(UserRole.USER).build()));

            jwtFilter.setUserDetailsToSecurityContextHolder(token);

            verify(securityContext).setAuthentication(any());
        }

    }

    @Test
    public void getAccessTokenFromRequestTest_correct() {
        var mockToken = UUID.randomUUID().toString();
        HttpServletRequest request = new Request(mock(), mock());
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + mockToken);

        var extractedToken = jwtFilter.getAccessTokenFromRequest(request);

        assertEquals(mockToken, extractedToken);
    }

    @Test
    public void getAccessTokenFromRequestTest_invalid() {
        var mockToken = UUID.randomUUID().toString();
        HttpServletRequest request = new Request(mock(), mock());
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(mockToken);

        var extractedToken = jwtFilter.getAccessTokenFromRequest(request);

        assertNull(extractedToken);
    }
}
