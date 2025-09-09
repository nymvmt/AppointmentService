package com.example.appointment.service;

import com.example.appointment.dto.AppointmentRequestDto;
import com.example.appointment.dto.AppointmentResponseDto;
import com.example.appointment.dto.AppointmentStatusUpdateDto;
import com.example.appointment.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 약속 Service 인터페이스 (Stub)
 */
public interface AppointmentService {
    
    /**
     * 약속 생성
     */
    AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto);
    
    /**
     * 전체 약속 목록 조회
     */
    List<AppointmentResponseDto> getAllAppointments();
    
    /**
     * 약속 상세 조회
     */
    AppointmentResponseDto getAppointmentById(String appointmentId);
    
    /**
     * 호스트의 약속 목록 조회
     */
    List<AppointmentResponseDto> getAppointmentsByHostId(String hostId);
    
    /**
     * 위치별 약속 목록 조회
     */
    List<AppointmentResponseDto> getAppointmentsByLocationId(String locationId);
    
    /**
     * 시작 시간별 약속 목록 조회
     */
    List<AppointmentResponseDto> getAppointmentsByStartTime(LocalDateTime startTime);
    
    /**
     * 종료 시간별 약속 목록 조회
     */
    List<AppointmentResponseDto> getAppointmentsByEndTime(LocalDateTime endTime);
    
    /**
     * 약속 상태 조회
     */
    Appointment.AppointmentStatus getAppointmentStatus(String appointmentId);
    
    /**
     * 약속 상태 변경
     */
    AppointmentResponseDto updateAppointmentStatus(String appointmentId, AppointmentStatusUpdateDto statusUpdateDto);
    
    /**
     * 약속 목록 조회 (필터링)
     */
    List<AppointmentResponseDto> getAppointmentsWithFilters(String locationId, 
                                                           Appointment.AppointmentStatus appointmentStatus,
                                                           LocalDateTime startTime, 
                                                           LocalDateTime endTime);
    
    /**
     * 약속 삭제
     */
    void deleteAppointment(String appointmentId);
}
