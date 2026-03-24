package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.UserWithPassword;
import com.jkefbq.gymentry.database.entity.User;
import com.jkefbq.gymentry.database.mapper.UserMapper;
import com.jkefbq.gymentry.database.repository.UserRepository;
import com.jkefbq.gymentry.security.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String CACHE_NAMES = "user";
    private final UserMapper userMapper;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    @Cacheable(cacheNames = CACHE_NAMES, key = "#dto?.email")
    public PartialUserDto create(UserWithPassword dto) {
        User notSavedUser = userMapper.toEntity(dto);
        notSavedUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        notSavedUser.setMemberSince(LocalDate.now());
        notSavedUser.setRole(UserRole.USER);
        notSavedUser.setTotalVisits(0);
        User savedUser = userRepo.save(notSavedUser);
        return userMapper.toPartialDto(savedUser);
    }

    @Override
    @Transactional
    public Optional<PartialUserDto> findByEmail(String email) {
        return userRepo.getUserByEmail(email).map(userMapper::toPartialDto);
    }

    @Override
    public Optional<UserWithPassword> findWithPasswordByEmail(String email) {
        return userRepo.getUserByEmail(email).map(userMapper::toDtoWithPassword);
    }

    @Override
    @Transactional
    public boolean isCorrectEmailAndPassword(String email, String passwordToCheck) {
        Optional<UserWithPassword> user = findWithPasswordByEmail(email);
        return user.isPresent() && passwordEncoder.matches(passwordToCheck, user.get().getPassword());
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    @CachePut(cacheNames = CACHE_NAMES, key = "#dto?.email")
    @Transactional
    public PartialUserDto update(PartialUserDto dto) {
        User notSavedEntity = userMapper.toEntity(dto);
        User savedEntity = userRepo.save(notSavedEntity);
        return userMapper.toPartialDto(savedEntity);
    }

    @Override
    public Optional<PartialUserDto> findById(UUID userId) {
        return userRepo.findById(userId).map(userMapper::toPartialDto);
    }

    @Override
    public Optional<UUID> getUserIdByEmail(String email) {
        return userRepo.getUserIdByEmail(email);
    }
}
