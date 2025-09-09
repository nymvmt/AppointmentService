package com.example.appointment.dto;

import com.example.appointment.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 약속 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    
    private String appointmentId;
    private String hostId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String locationId;
    private Appointment.AppointmentStatus appointmentStatus;
    
    // 감사 정보 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
