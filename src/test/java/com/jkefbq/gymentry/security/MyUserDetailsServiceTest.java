package com.jkefbq.gymentry.security;

import com.jkefbq.gymentry.dto.for_entity.UserWithPassword;
import com.jkefbq.gymentry.service.database.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyUserDetailsServiceTest {

    @Mock
    UserService userService;

    @InjectMocks
    MyUserDetailsService userDetailsService;

    @Test
    public void loadUserByUserNameTest() {
        var email = "email";
        var user = UserWithPassword.builder().email(email).role(UserRole.USER).password("password").build();
        when(userService.findWithPasswordByEmail(any())).thenReturn(Optional.ofNullable(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals("ROLE_" + user.getRole().name(),
                userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority());
    }
}
