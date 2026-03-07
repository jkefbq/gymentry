package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.database.dto.PartialUserDto;
import com.jkefbq.gymentry.database.dto.UserDto;
import com.jkefbq.gymentry.database.mapper.UserMapper;
import com.jkefbq.gymentry.database.service.SubscriptionManager;
import com.jkefbq.gymentry.database.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserWithSubsFacadeTest {

    @Mock
    UserMapper userMapper;
    @Mock
    UserService userService;
    @Mock
    SubscriptionManager subscriptionManager;

    @InjectMocks
    UserWithSubsFacade userWithSubsFacade;

    @Test
    void findByEmailTest() {
        var email = "test@gmail.com";
        when(userService.findByEmail(email)).thenReturn(Optional.of(new PartialUserDto()));
        when(userMapper.toFullDto(any())).thenReturn(new UserDto());

        userWithSubsFacade.findByEmail(email);

        verify(userService).findByEmail(email);
        verify(subscriptionManager).getUserSubs(any());
        verify(userMapper).toFullDto(any());
    }

    @Test
    void findByUserIdTest() {
        var userId = UUID.randomUUID();
        when(userService.findById(any())).thenReturn(Optional.of(new PartialUserDto()));
        when(userMapper.toFullDto(any())).thenReturn(new UserDto());

        userWithSubsFacade.findByUserId(userId);

        verify(userService).findById(any());
        verify(subscriptionManager).getUserSubs(any());
        verify(userMapper).toFullDto(any());
    }
}