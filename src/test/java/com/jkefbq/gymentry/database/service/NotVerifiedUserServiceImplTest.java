package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.NotVerifiedUserDto;
import com.jkefbq.gymentry.database.mapper.NotVerifiedUserMapper;
import com.jkefbq.gymentry.database.repository.NotVerifiedUserRepository;
import com.jkefbq.gymentry.service.database.NotVerifiedUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotVerifiedUserServiceImplTest {

    private static final String EMAIL = "email@gmail.com";

    @Mock
    NotVerifiedUserRepository repository;
    @Mock
    NotVerifiedUserMapper mapper;

    @InjectMocks
    NotVerifiedUserServiceImpl notVerUserService;

    public NotVerifiedUserDto getUserDto() {
        return new NotVerifiedUserDto(
                UUID.randomUUID(),
                "firstname", EMAIL, "password"
        );
    }

    @Test
    public void createTest() {
        notVerUserService.create(getUserDto());

        verify(repository).save(any());
    }

    @Test
    public void existsByEmailTest() {
        notVerUserService.existsByEmail(EMAIL);

        verify(repository).existsByEmail(EMAIL);
    }

    @Test
    public void findUserByEmailTest() {
        notVerUserService.findUserByEmail(EMAIL);

        verify(repository).getByEmail(EMAIL);
    }

    @Test
    public void deleteByEmailTest() {
        notVerUserService.deleteByEmail(EMAIL);

        verify(repository).deleteByEmail(EMAIL);
    }
}
