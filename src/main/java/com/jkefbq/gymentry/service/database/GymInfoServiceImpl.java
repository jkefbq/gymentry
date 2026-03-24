package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.GymInfoDto;
import com.jkefbq.gymentry.database.entity.GymInfo;
import com.jkefbq.gymentry.database.mapper.GymInfoMapper;
import com.jkefbq.gymentry.database.repository.GymInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GymInfoServiceImpl implements GymInfoService {

    private static final String CACHE_NAMES = "gyms";
    private final GymInfoRepository gymInfoRepository;
    private final GymInfoMapper gymInfoMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CachePut(cacheNames = CACHE_NAMES, unless = "#result == null")
    @Override
    public GymInfoDto save(GymInfoDto dto) {
        GymInfo notSavedEntity = gymInfoMapper.toEntity(dto);
        GymInfo savedEntity = gymInfoRepository.save(notSavedEntity);
        return gymInfoMapper.toDto(savedEntity);
    }

    @Transactional
    @Override
    @Cacheable(cacheNames = CACHE_NAMES)
    public List<String> getAllAddresses() {
        return gymInfoRepository.getAllAddresses();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public Optional<GymInfoDto> getByAddress(String gymAddress) {
        return gymInfoRepository.getByAddress(gymAddress).map(gymInfoMapper::toDto);
    }

}
