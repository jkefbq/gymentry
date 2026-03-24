package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.TariffDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.database.mapper.TariffMapper;
import com.jkefbq.gymentry.database.repository.TariffRepository;
import com.jkefbq.gymentry.service.database.TariffServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TariffServiceImplTest {

    @Mock
    TariffRepository tariffRepository;
    @Mock
    TariffMapper tariffMapper;

    @InjectMocks
    TariffServiceImpl tariffService;

    @Test
    public void getAllTest() {
        tariffService.getAll();

        verify(tariffRepository).findAll();
    }

    @Test
    public void getByTypeTest() {
        TariffType type = Arrays.stream(TariffType.values()).findAny().orElse(TariffType.BASIC);
        tariffService.getByType(type);

        verify(tariffRepository).getByTariffType(type);
    }

    @Test
    public void saveAllTest() {
        tariffService.saveAll(List.of(new TariffDto()));

        verify(tariffRepository).saveAll(any());
    }

    @Test
    public void createTest() {
        tariffService.create(new TariffDto());

        verify(tariffRepository).save(any());
    }

    @Test
    public void deleteAllTest() {
        tariffService.deleteAll(List.of(new TariffDto()));

        verify(tariffRepository).deleteAll(any());
    }

}