package com.jkefbq.gymentry.dto.for_entity;

import com.jkefbq.gymentry.security.UserRole;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartialUserDto implements Serializable {
    private UUID id;
    private String firstName;
    @Email
    private String email;
    private UserRole role;
    private Integer totalVisits;
    private LocalDate memberSince;
    private LocalDate lastVisit;
}
