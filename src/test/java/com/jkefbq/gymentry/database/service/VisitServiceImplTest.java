package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.GymInfoDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.database.mapper.VisitMapper;
import com.jkefbq.gymentry.database.repository.VisitRepository;
import com.jkefbq.gymentry.service.database.VisitServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplTest {

    @Mock
    VisitRepository visitRepository;
    @Mock
    VisitMapper visitMapper;

    @InjectMocks
    VisitServiceImpl visitServiceImpl;

    public VisitDto getVisitDto() {
        return VisitDto.builder().id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .subscription(new SubscriptionDto())
                .gym(new GymInfoDto())
                .build();
    }

    @Test
    public void createTest() {
        visitServiceImpl.create(new VisitDto());
        verify(visitRepository).save(any());
    }

    @Test
    public void getAllTest() {
        visitServiceImpl.getAll();
        verify(visitRepository).findAll();
    }

    @Test
    public void getAllForPeriod() {
        var from = LocalDateTime.now();
        var to = LocalDateTime.now();
        var address = "address";

        visitServiceImpl.getAllForPeriod(from, to, address);

        verify(visitRepository).getAllForPeriod(from, to, address);
    }

}
