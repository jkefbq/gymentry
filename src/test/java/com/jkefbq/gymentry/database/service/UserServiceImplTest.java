package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.UserDto;
import com.jkefbq.gymentry.dto.for_entity.UserWithPassword;
import com.jkefbq.gymentry.database.entity.User;
import com.jkefbq.gymentry.database.mapper.UserMapper;
import com.jkefbq.gymentry.database.mapper.UserMapperImpl;
import com.jkefbq.gymentry.database.repository.UserRepository;
import com.jkefbq.gymentry.security.UserRole;
import com.jkefbq.gymentry.service.database.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Captor
    ArgumentCaptor<User> captor;

    @Spy
    UserMapper userMapper = new UserMapperImpl();
    @Mock
    UserRepository userRepo;
    @Mock
    PasswordEncoder passwordEncoder;

    @Spy
    @InjectMocks
    UserServiceImpl userService;

    public UserWithPassword getUserWithPassword() {
        return UserWithPassword.builder()
                .memberSince(LocalDate.now())
                .role(UserRole.USER)
                .totalVisits(1)
                .password("password")
                .email("email@gmail.com")
                .build();
    }

    public PartialUserDto getPartialUser() {
        return PartialUserDto.builder()
                .memberSince(LocalDate.now())
                .role(UserRole.USER)
                .totalVisits(1)
                .email("email@gmail.com")
                .build();
    }

    @Test
    public void createTest() {
        when(passwordEncoder.encode(any())).thenReturn(UUID.randomUUID().toString());
        var user = getUserWithPassword();
        user.setMemberSince(LocalDate.now().plusDays(3));
        user.setRole(UserRole.ADMIN);

        userService.create(user);

        verify(userRepo).save(captor.capture());
        assertNotEquals(captor.getValue().getMemberSince(), user.getMemberSince());
        assertNotEquals(captor.getValue().getPassword(), user.getPassword());
        assertEquals(UserRole.USER, captor.getValue().getRole());
    }

    @Test
    public void findByEmailTest() {
        var email = "email@gmail.com";
        userService.findByEmail(email);

        verify(userRepo).getUserByEmail(email);
    }

    @Test
    public void isCorrectEmailAndPasswordTest_assertFalse() {
        var password = UUID.randomUUID().toString();
        UserDto user = new UserDto();
        user.setPassword(password);

        boolean isMatches = userService.isCorrectEmailAndPassword("email", UUID.randomUUID().toString());

        assertFalse(isMatches);
    }

    @Test
    public void isCorrectEmailAndPasswordTest_assertTrue() {
        var password = UUID.randomUUID().toString();
        UserWithPassword user = new UserWithPassword();
        user.setPassword(password);
        doReturn(Optional.of(user)).when(userService).findWithPasswordByEmail(any());
        doAnswer(invocation ->
            invocation.getArgument(0).equals(invocation.getArgument(1))
        ).when(passwordEncoder).matches(any(), any());

        boolean isMatches = userService.isCorrectEmailAndPassword("email", password);

        assertTrue(isMatches);
    }

    @Test
    public void existsByEmailTest() {
        var email = "email@gmail.com";

        userService.existsByEmail(email);

        verify(userRepo).existsByEmail(email);
    }

    @Test
    public void updateTest() {
        userService.update(getPartialUser());

        verify(userRepo).save(any());
    }
}
