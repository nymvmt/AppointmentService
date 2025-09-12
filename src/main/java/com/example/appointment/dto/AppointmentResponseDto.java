package com.example.appointment.dto;

import com.example.appointment.entity.Appointment;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    
    @JsonProperty("appointment_id")
    private String appointmentId;
    
    @JsonProperty("host_id")
    private String hostId;
    
    @JsonProperty("host_username")
    private String hostUsername;
    
    @JsonProperty("host_nickname")
    private String hostNickname;
    
    private String title;
    private String description;
    
    @JsonProperty("start_time")
    private LocalDateTime startTime;
    
    @JsonProperty("end_time")
    private LocalDateTime endTime;
    
    @JsonProperty("location_id")
    private String locationId;
    
    @JsonProperty("appointment_status")
    private Appointment.AppointmentStatus appointmentStatus;
    
    private String feedback; // 피드백 상태 (F: 미완료, T: 완료)
}
