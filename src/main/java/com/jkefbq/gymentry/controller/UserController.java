package com.jkefbq.gymentry.controller;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.UserService;
import com.jkefbq.gymentry.exception.InvalidSubscriptionException;
import com.jkefbq.gymentry.exception.NonActiveSubscriptionException;
import com.jkefbq.gymentry.facade.GymEntryFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final GymEntryFacade gymEntryFacade;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @GetMapping("me")
    public PartialUserDto getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("call '/user/me', user with email {}", userDetails.getUsername());
        return userService.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping("subscriptions")
    public List<SubscriptionDto> getAllSubscriptions(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("call '/user/subscriptions', user with email {}", userDetails.getUsername());
        var userId = userService.getUserIdByEmail(userDetails.getUsername()).orElseThrow();
        return subscriptionService.getUserSubs(userId);
    }

    @GetMapping("subscriptions/active")
    public SubscriptionDto getActiveSubscription(@AuthenticationPrincipal UserDetails userDetails) throws NonActiveSubscriptionException {
        log.info("call '/user/subscriptions/active', user with email {}", userDetails.getUsername());
        var userId = userService.getUserIdByEmail(userDetails.getUsername()).orElseThrow();
        return subscriptionService.getActiveSubscription(userId);
    }

    @PostMapping("subscriptions/activate")
    public SubscriptionDto activateSubscription(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody UUID subscriptionId) {
        log.info("call '/user/subscriptions/activate', user with email {}, subscription id {}", userDetails.getUsername(), subscriptionId);
        return subscriptionService.activateSubscription(subscriptionId);
    }

    @PutMapping("/entry")
    public String getGymEntryCode(@AuthenticationPrincipal UserDetails userDetails) throws NonActiveSubscriptionException, InvalidSubscriptionException {
        log.info("call '/user/entry', user with email {}", userDetails.getUsername());
        return gymEntryFacade.tryEntry(userDetails.getUsername());
    }

    @PostMapping("subscriptions/deactivate")
    public SubscriptionDto deactivate(@RequestBody UUID subscriptionId) {
        log.info("call 'user/subscriptions/deactivate', subscription id {}", subscriptionId);
        return subscriptionService.deactivateSubscription(subscriptionId);
    }
}