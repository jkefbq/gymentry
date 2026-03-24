package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.GymInfoDto;

import java.util.List;
import java.util.Optional;

public interface GymInfoService {
    GymInfoDto save(GymInfoDto dto);
    List<String> getAllAddresses();
    Optional<GymInfoDto> getByAddress(String gymAddress);
}
