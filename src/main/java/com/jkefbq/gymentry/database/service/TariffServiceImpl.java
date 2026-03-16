package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.database.dto.TariffDto;
import com.jkefbq.gymentry.database.dto.TariffType;
import com.jkefbq.gymentry.database.entity.Tariff;
import com.jkefbq.gymentry.database.mapper.TariffMapper;
import com.jkefbq.gymentry.database.repository.TariffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private static final String CACHE_NAMES = "tariffs";
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    @Override
    @Transactional
    @Cacheable(cacheNames = CACHE_NAMES)
    public List<TariffDto> getAll() {
        return tariffRepository.findAll().stream()
                .map(tariffMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public Optional<TariffDto> getByType(TariffType type) {
        return tariffRepository.getByTariffType(type)
                .map(tariffMapper::toDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @CacheEvict(cacheNames = CACHE_NAMES, allEntries = true)
    @Transactional
    public List<TariffDto> saveAll(List<TariffDto> tariffList) {
        List<Tariff> entityList = tariffList.stream().map(tariffMapper::toEntity).toList();
        return tariffRepository.saveAll(entityList).stream().map(tariffMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @CacheEvict(cacheNames = CACHE_NAMES, allEntries = true)
    @Transactional
    public TariffDto create(TariffDto tariffDto) {
        Tariff notSavedEntity = tariffMapper.toEntity(tariffDto);
        Tariff savedEntity = tariffRepository.save(notSavedEntity);
        return tariffMapper.toDto(savedEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @CacheEvict(cacheNames = CACHE_NAMES, allEntries = true)
    @Transactional
    public void deleteAll(List<TariffDto> tariffs) {
        List<Tariff> entities = tariffs.stream().map(tariffMapper::toEntity).toList();
        tariffRepository.deleteAll(entities);
    }

}
