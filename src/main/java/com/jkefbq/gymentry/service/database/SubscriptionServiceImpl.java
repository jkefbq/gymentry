package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.database.entity.Subscription;
import com.jkefbq.gymentry.database.mapper.SubscriptionMapper;
import com.jkefbq.gymentry.database.repository.SubscriptionRepository;
import com.jkefbq.gymentry.dto.auth.EmailDto;
import com.jkefbq.gymentry.dto.statistics.PurchaseDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionRequestDto;
import com.jkefbq.gymentry.exception.NonActiveSubscriptionException;
import com.jkefbq.gymentry.exception.SubscriptionAlreadyActiveException;
import com.jkefbq.gymentry.exception.VisitsAreOverException;
import com.jkefbq.gymentry.service.MessageProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final String CACHE_NAMES = "subs";
    private static final String CACHE_NAMES_ACTIVE_SUB = "active-subs";
    private static final String CACHE_NAMES_ALL_USER = "user-subs";
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final MessageProducer messageProducer;

    @Override
    @Transactional
    @Caching(
            put   = @CachePut(cacheNames = CACHE_NAMES, key = "#result?.id?.toString()", unless = "#result == null"),
            evict = @CacheEvict(cacheNames = CACHE_NAMES_ALL_USER, key = "#result?.userId?.toString()")
    )
    public SubscriptionDto sendCreateMessage(SubscriptionDto dto) {
        refreshSubscriptionData(dto);
        Subscription notSavedEntity = subscriptionMapper.toEntity(dto);
        Subscription savedEntity = subscriptionRepository.save(notSavedEntity);
        return subscriptionMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public void sendCreateMessage(SubscriptionRequestDto requestDto, String email) {
        PurchaseDto purchase = new PurchaseDto(
                requestDto.getVisitsTotal(), requestDto.getTariffType(), new EmailDto(email)
        );
        messageProducer.sendSubscriptionPurchase(purchase);
    }

    @Override
    @Transactional
    public SubscriptionDto update(SubscriptionDto dto) {
        refreshSubscriptionData(dto);
        Subscription notSavedEntity = subscriptionMapper.toEntity(dto);
        Subscription savedEntity = subscriptionRepository.save(notSavedEntity);
        return subscriptionMapper.toDto(savedEntity);
    }

    @Transactional
    @Cacheable(cacheNames = CACHE_NAMES_ACTIVE_SUB, key = "#userId", unless = "#result == null")
    @Override
    public SubscriptionDto getActiveSubscription(UUID userId) {
        return getUserSubs(userId).stream()
                .filter(SubscriptionDto::getActive)
                .reduce((e1, e2) -> {
                    throw new IllegalStateException("user can't have more then 1 active subscription");
                })
                .orElseThrow(() -> new NonActiveSubscriptionException("the user does not have any active subscriptions"));
    }

    @Transactional
    @Cacheable(cacheNames = CACHE_NAMES_ALL_USER, key = "#userId", unless = "#result == null")
    @Override
    public List<SubscriptionDto> getUserSubs(UUID userId) {
        return subscriptionRepository.findByUser_Id(userId).stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }

    @Transactional
    @Caching(
            put = @CachePut(cacheNames = CACHE_NAMES, key = "#result?.id?.toString()", unless = "#result == null"),
            evict = {
                    @CacheEvict(cacheNames = CACHE_NAMES_ALL_USER, key = "#result?.userId"),
                    @CacheEvict(cacheNames = CACHE_NAMES_ACTIVE_SUB, key = "#result?.userId")
            }
    )
    @Override
    public SubscriptionDto activateSubscription(UUID subscriptionId) {
        var sub = findById(subscriptionId).orElseThrow();
        canSubActivate(sub);
        sub.setActive(true);
        return update(sub);
    }

    @Override
    @Transactional
    public List<SubscriptionDto> getAllForPeriod(LocalDate from, LocalDate to) {
        return subscriptionRepository.getAllForPeriod(from, to).stream().map(subscriptionMapper::toDto).toList();
    }


    @Transactional
    @Override
    public Optional<SubscriptionDto> findById(UUID id) {
        return subscriptionRepository.findById(id).map(subscriptionMapper::toDto);
    }

    @Transactional
    @Override
    @Caching(
            put = @CachePut(cacheNames = CACHE_NAMES, key = "#result?.id?.toString()", unless = "#result == null"),
            evict = {
                    @CacheEvict(cacheNames = CACHE_NAMES_ALL_USER, key = "#result?.userId"),
                    @CacheEvict(cacheNames = CACHE_NAMES_ACTIVE_SUB, key = "#result?.userId")
            }
    )
    public SubscriptionDto deactivateSubscription(UUID subscriptionId) {
        var sub = findById(subscriptionId).orElseThrow();
        checkActiveSub(sub);
        sub.setActive(false);
        return update(sub);
    }

    @Override
    public void checkActiveSub(SubscriptionDto sub) {
        if (!sub.getActive()) {
            throw new IllegalStateException("subscription is not active");
        }
    }

    private void refreshSubscriptionData(SubscriptionDto subscriptionDto) {
        if (subscriptionDto.getVisitsLeft() <= 0) {
            subscriptionDto.setActive(false);
        }
    }

    private void canSubActivate(SubscriptionDto sub) {
        if (sub.getActive()) {
            throw new SubscriptionAlreadyActiveException();
        }
        if (sub.getVisitsLeft() <= 0) {
            throw new VisitsAreOverException();
        }
    }

}
