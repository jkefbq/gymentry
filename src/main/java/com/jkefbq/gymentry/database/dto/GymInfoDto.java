package com.jkefbq.gymentry.database.dto;

import com.jkefbq.gymentry.dto.CanCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@CanCache
public class GymInfoDto implements Serializable {
    private String address;
}