package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.service.database.GymInfoService;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.UserService;
import com.jkefbq.gymentry.service.database.VisitService;
import com.jkefbq.gymentry.exception.NonActiveSubscriptionException;
import com.jkefbq.gymentry.service.EntryCodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GymEntryFacade {

    private final UserService userService;
    private final EntryCodeService entryCodeService;
    private final SubscriptionService subscriptionService;
    private final GymInfoService gymInfoService;
    private final VisitService visitService;

    @Transactional
    public String tryEntry(String email) {
        var userId = userService.findByEmail(email).orElseThrow().getId();
        var subs = subscriptionService.getUserSubs(userId);
        var activeSub = subscriptionService.getActiveSubscription(userId);
        checkAllSubs(subs);
        subscriptionService.checkActiveSub(activeSub);

        return entryCodeService.generateUserEntryCode(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void confirmEntry(String code, String email, String gymAddress) {
        var activeSub = findAndDecrementSub(code);
        var user = refreshUser(email);
        createVisit(gymAddress, activeSub);

        subscriptionService.update(activeSub);
        userService.update(user);
    }

    public PartialUserDto refreshUser(String email) {
        var user = userService.findByEmail(email).orElseThrow();
        user.setLastVisit(LocalDate.now());
        user.setTotalVisits(user.getTotalVisits() + 1);
        return user;
    }

    public SubscriptionDto findAndDecrementSub(String code) {
        var activeSub = findActiveSubscription(code);
        activeSub.setVisitsLeft(activeSub.getVisitsLeft() - 1);
        return activeSub;
    }

    public SubscriptionDto findActiveSubscription(String code) {
        var email = entryCodeService.getEmailByCode(code);
        var userId = userService.findByEmail(email).orElseThrow().getId();
        return subscriptionService.getActiveSubscription(userId);
    }

    public void createVisit(String gymAddress, SubscriptionDto activeSub) {
        var gymInfoDto = gymInfoService.getByAddress(gymAddress).orElseThrow();
        visitService.create(VisitDto.builder()
                .gym(gymInfoDto)
                .createdAt(LocalDateTime.now())
                .subscription(activeSub)
                .build());
    }

    public void checkAllSubs(List<SubscriptionDto> subs) {
        subs.stream()
                .filter(SubscriptionDto::getActive)
                .reduce((s1, s2) -> {
                    throw new IllegalStateException("user have more then 1 active subscriptions");
                })
                .orElseThrow(NonActiveSubscriptionException::new);
    }

}
