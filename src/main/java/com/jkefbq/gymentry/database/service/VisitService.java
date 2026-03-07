package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.database.dto.VisitDto;
import com.jkefbq.gymentry.database.entity.Visit;
import com.jkefbq.gymentry.database.mapper.VisitMapper;
import com.jkefbq.gymentry.database.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService implements VisitManager {

    private static final String CACHE_NAMES = "visits";
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;

    @Transactional
    @Override
    @CacheEvict(cacheNames = CACHE_NAMES, allEntries = true)
    public VisitDto create(VisitDto dto) {
        Visit notSavedEntity = visitMapper.toEntity(dto);
        Visit savedEntity = visitRepository.save(notSavedEntity);
        return visitMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @Cacheable(cacheNames = CACHE_NAMES, unless = "#result == null")
    public List<VisitDto> getAll() {
        return visitRepository.findAll().stream().map(visitMapper::toDto).toList();
    }

    @Transactional
    @Override
    public List<VisitDto> getAllForPeriod(LocalDateTime from, LocalDateTime to, String address) {
        return visitRepository.getAllForPeriod(from, to, address).stream().map(visitMapper::toDto).toList();
    }

}