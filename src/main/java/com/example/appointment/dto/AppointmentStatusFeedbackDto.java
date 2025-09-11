package com.example.appointment.dto;

import com.example.appointment.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 약속 상태 및 피드백 정보 DTO (프론트 요청용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusFeedbackDto {
    
    private Appointment.AppointmentStatus appointmentStatus;
    private String feedback; // F: 미완료, T: 완료
    private List<GuestInfo> guests; // Guest 정보들
}