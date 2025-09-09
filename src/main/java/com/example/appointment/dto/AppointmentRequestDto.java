package com.example.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 약속 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDto {
    
    private String hostId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String locationId;
}
