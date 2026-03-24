package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.UserDto;
import com.jkefbq.gymentry.database.mapper.UserMapper;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.UserService;
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
    SubscriptionService subscriptionService;

    @InjectMocks
    UserWithSubsFacade userWithSubsFacade;

    @Test
    void findByEmailTest() {
        var email = "test@gmail.com";
        when(userService.findByEmail(email)).thenReturn(Optional.of(new PartialUserDto()));
        when(userMapper.toFullDto(any())).thenReturn(new UserDto());

        userWithSubsFacade.findByEmail(email);

        verify(userService).findByEmail(email);
        verify(subscriptionService).getUserSubs(any());
        verify(userMapper).toFullDto(any());
    }

    @Test
    void findByUserIdTest() {
        var userId = UUID.randomUUID();
        when(userService.findById(any())).thenReturn(Optional.of(new PartialUserDto()));
        when(userMapper.toFullDto(any())).thenReturn(new UserDto());

        userWithSubsFacade.findByUserId(userId);

        verify(userService).findById(any());
        verify(subscriptionService).getUserSubs(any());
        verify(userMapper).toFullDto(any());
    }
}