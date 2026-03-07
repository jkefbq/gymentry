package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.database.dto.UserDto;
import com.jkefbq.gymentry.database.mapper.UserMapper;
import com.jkefbq.gymentry.database.service.SubscriptionManager;
import com.jkefbq.gymentry.database.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserWithSubsFacade implements UserWithSubsProvider {

    private final UserMapper userMapper;
    private final UserService userService;
    private final SubscriptionManager subscriptionManager;

    @Transactional
    @Override
    public UserDto findByEmail(String email) {
        var partialUser = userService.findByEmail(email).orElseThrow();
        var subs = subscriptionManager.getUserSubs(partialUser.getId());
        var fullUser = userMapper.toFullDto(partialUser);
        fullUser.setSubscriptions(subs);
        return fullUser;
    }

    @Transactional
    @Override
    public UserDto findByUserId(UUID userId) {
        var partialUser = userService.findById(userId).orElseThrow();
        var subs = subscriptionManager.getUserSubs(userId);
        var fullUser = userMapper.toFullDto(partialUser);
        fullUser.setSubscriptions(subs);
        return fullUser;
    }

}
