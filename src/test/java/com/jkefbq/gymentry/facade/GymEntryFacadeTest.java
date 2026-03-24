package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.GymInfoDto;
import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.service.database.GymInfoService;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.UserService;
import com.jkefbq.gymentry.service.database.VisitService;
import com.jkefbq.gymentry.exception.NonActiveSubscriptionException;
import com.jkefbq.gymentry.service.EntryCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymEntryFacadeTest {

    private static final String MOCK_EMAIL = "email@gmail.com";

    @Mock
    UserService userService;
    @Mock
    EntryCodeService entryCodeService;
    @Mock
    SubscriptionService subscriptionService;
    @Mock
    GymInfoService gymInfoService;
    @Mock
    VisitService visitService;

    @Spy
    @InjectMocks
    GymEntryFacade gymEntryFacade;

    @Test
    public void tryEntryTest() {
        when(userService.findByEmail(MOCK_EMAIL)).thenReturn(Optional.of(PartialUserDto.builder().id(UUID.randomUUID()).build()));
        when(subscriptionService.getUserSubs(any())).thenReturn(List.of());
        when(subscriptionService.getActiveSubscription(any())).thenReturn(new SubscriptionDto());
        doNothing().when(subscriptionService).checkActiveSub(any());
        doNothing().when(gymEntryFacade).checkAllSubs(any());

        gymEntryFacade.tryEntry(MOCK_EMAIL);

        verify(subscriptionService).getActiveSubscription(any());
        verify(entryCodeService).generateUserEntryCode(MOCK_EMAIL);
    }

    @Test
    public void confirmEntryTest() {
        doReturn(new SubscriptionDto()).when(gymEntryFacade).findAndDecrementSub(any());
        doReturn(new PartialUserDto()).when(gymEntryFacade).refreshUser(any());
        doNothing().when(gymEntryFacade).createVisit(any(), any());
        var entryCode = RandomStringUtils.randomNumeric(6);

        gymEntryFacade.confirmEntry(entryCode, MOCK_EMAIL, "address");

        verify(userService).update(any());
        verify(subscriptionService).update(any());
    }

    @Test
    public void refreshUserTest() {
        Integer totalVisitsBefore = 10;
        when(userService.findByEmail(MOCK_EMAIL))
                .thenReturn(Optional.of(PartialUserDto.builder().totalVisits(totalVisitsBefore).build()));

        var user = gymEntryFacade.refreshUser(MOCK_EMAIL);

        assertEquals(totalVisitsBefore + 1, user.getTotalVisits());
        assertEquals(LocalDate.now(), user.getLastVisit());
    }

    @Test
    public void findAndDecrementSubTest() {
        var entryCode = RandomStringUtils.randomNumeric(6);
        var visitsLeft = 10;
        var sub = SubscriptionDto.builder().active(true).visitsLeft(visitsLeft).visitsTotal(12).build();
        doAnswer(invocation -> sub).when(gymEntryFacade).findActiveSubscription(entryCode);

        var decrementSub = gymEntryFacade.findAndDecrementSub(entryCode);

        assertEquals(visitsLeft, decrementSub.getVisitsLeft() + 1);
    }

    @Test
    public void findActiveSubscriptionTest() {
        var entryCode = RandomStringUtils.randomNumeric(6);
        when(userService.findByEmail(any())).thenReturn(Optional.of(new PartialUserDto()));

        gymEntryFacade.findActiveSubscription(entryCode);

        verify(entryCodeService).getEmailByCode(entryCode);
        verify(userService).findByEmail(any());
        verify(subscriptionService).getActiveSubscription(any());
    }

    @Test
    public void createVisitTest() {
        ArgumentCaptor<VisitDto> captor = ArgumentCaptor.forClass(VisitDto.class);
        var activeSub = SubscriptionDto.builder().active(true).build();
        var gym = new GymInfoDto("address");
        when(gymInfoService.getByAddress(gym.getAddress())).thenReturn(Optional.of(gym));

        gymEntryFacade.createVisit(gym.getAddress(), activeSub);

        verify(gymInfoService).getByAddress(gym.getAddress());
        verify(visitService).create(captor.capture());
        assertEquals(activeSub, captor.getValue().getSubscription());
        assertEquals(gym, captor.getValue().getGym());
        assertEquals(LocalDate.now(), captor.getValue().getCreatedAt().toLocalDate());
    }

    @Test
    public void checkAllSubsTest_nonActiveSubs() {
        var subs = List.of(SubscriptionDto.builder().active(false).build(),
                SubscriptionDto.builder().active(false).build(), SubscriptionDto.builder().active(false).build());

        assertThrows(NonActiveSubscriptionException.class, () -> gymEntryFacade.checkAllSubs(subs));
    }

    @Test
    public void checkAllSubsTest_manyActiveSubs() {
        var subs = List.of(SubscriptionDto.builder().active(true).build(),
                SubscriptionDto.builder().active(true).build(), SubscriptionDto.builder().active(false).build());

        assertThrows(IllegalStateException.class, () -> gymEntryFacade.checkAllSubs(subs));
    }

}
