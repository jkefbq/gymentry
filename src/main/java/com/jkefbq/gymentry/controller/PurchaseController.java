package com.jkefbq.gymentry.controller;

import com.jkefbq.gymentry.dto.for_entity.TariffDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.service.database.SubscriptionServiceImpl;
import com.jkefbq.gymentry.service.database.TariffService;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionRequestDto;
import com.jkefbq.gymentry.service.SubscriptionPriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
public class PurchaseController {

    private final TariffService tariffService;
    private final SubscriptionPriceCalculator subscriptionPriceCalculator;
    private final SubscriptionServiceImpl subscriptionServiceImpl;

    @GetMapping
    public List<TariffDto> getAllTariffs() {
        log.info("call /market");
        return tariffService.getAll();
    }

    @GetMapping("/calculate-price/{tariff-type}/{visits}")
    public BigDecimal calculatePrice(
            @PathVariable("tariff-type") TariffType tariffType,
            @PathVariable("visits") Integer visitsCount
    ) {
        log.info("call /market/calculate-price/{}/{}", tariffType, visitsCount);
        return subscriptionPriceCalculator.calculate(tariffType, visitsCount);
    }

    @PostMapping("/subscription")
    public ResponseEntity<?> create(
            @RequestBody SubscriptionRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("call /market/subscription");
        subscriptionServiceImpl.sendCreateMessage(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}