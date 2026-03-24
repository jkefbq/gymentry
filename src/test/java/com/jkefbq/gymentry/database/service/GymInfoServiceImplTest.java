package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.GymInfoDto;
import com.jkefbq.gymentry.database.mapper.GymInfoMapper;
import com.jkefbq.gymentry.database.repository.GymInfoRepository;
import com.jkefbq.gymentry.service.database.GymInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GymInfoServiceImplTest {

    @Mock
    GymInfoRepository gymInfoRepository;
    @Mock
    GymInfoMapper gymInfoMapper;

    @InjectMocks
    GymInfoServiceImpl gymInfoService;

    public GymInfoDto getGym() {
        return new GymInfoDto("address of gym");
    }

    @Test
    public void saveTest() {
        GymInfoDto gym = getGym();

        gymInfoService.save(gym);

        verify(gymInfoRepository).save(any());
    }

    @Test
    public void getAllAddressesTest() {
        gymInfoService.getAllAddresses();

        verify(gymInfoRepository).getAllAddresses();
    }

    @Test
    public void getByAddressTest() {
        var address = "address";

        gymInfoService.getByAddress(address);

        verify(gymInfoRepository).getByAddress(address);
    }

}
