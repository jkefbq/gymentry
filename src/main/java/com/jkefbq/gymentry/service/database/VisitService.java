package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.VisitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitService {
    VisitDto create(VisitDto dto);
    List<VisitDto> getAll();
    List<VisitDto> getAllForPeriod(LocalDateTime from, LocalDateTime to, String address);
}
