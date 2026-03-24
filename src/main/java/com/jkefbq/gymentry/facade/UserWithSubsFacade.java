package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.UserDto;
import com.jkefbq.gymentry.database.mapper.UserMapper;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserWithSubsFacade {

    private final UserMapper userMapper;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public UserDto findByEmail(String email) {
        var partialUser = userService.findByEmail(email).orElseThrow();
        var subs = subscriptionService.getUserSubs(partialUser.getId());
        var fullUser = userMapper.toFullDto(partialUser);
        fullUser.setSubscriptions(subs);
        return fullUser;
    }

    @Transactional
    public UserDto findByUserId(UUID userId) {
        var partialUser = userService.findById(userId).orElseThrow();
        var subs = subscriptionService.getUserSubs(userId);
        var fullUser = userMapper.toFullDto(partialUser);
        fullUser.setSubscriptions(subs);
        return fullUser;
    }

}
