package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.NotVerifiedUserDto;
import com.jkefbq.gymentry.database.entity.NotVerifiedUser;
import com.jkefbq.gymentry.database.mapper.NotVerifiedUserMapper;
import com.jkefbq.gymentry.database.repository.NotVerifiedUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotVerifiedUserServiceImpl implements NotVerifiedUserService {

    private final NotVerifiedUserRepository repository;
    private final NotVerifiedUserMapper mapper;

    @Override
    @Transactional
    public NotVerifiedUserDto create(NotVerifiedUserDto dto) {
        NotVerifiedUser notSavedEntity = mapper.toEntity(dto);
        NotVerifiedUser savedEntity = repository.save(notSavedEntity);
        return mapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Optional<NotVerifiedUserDto> findUserByEmail(String email) {
        return repository.getByEmail(email).map(mapper::toDto);
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        repository.deleteByEmail(email);
    }

}
