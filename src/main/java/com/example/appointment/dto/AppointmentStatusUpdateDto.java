package com.example.appointment.dto;

import com.example.appointment.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 약속 상태 변경 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusUpdateDto {
    
    private Appointment.AppointmentStatus appointmentStatus;
}
