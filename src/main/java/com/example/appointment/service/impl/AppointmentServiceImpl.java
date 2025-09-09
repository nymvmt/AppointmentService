package com.example.appointment.service.impl;

import com.example.appointment.dto.AppointmentRequestDto;
import com.example.appointment.dto.AppointmentResponseDto;
import com.example.appointment.dto.AppointmentStatusUpdateDto;
import com.example.appointment.entity.Appointment;
import com.example.appointment.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 약속 Service 구현체 (Mock 데이터 사용)
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {
    
    @Override
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        // Mock 데이터 생성
        AppointmentResponseDto response = new AppointmentResponseDto();
        response.setAppointmentId(UUID.randomUUID().toString());
        response.setHostId(requestDto.getHostId());
        response.setTitle(requestDto.getTitle());
        response.setDescription(requestDto.getDescription());
        response.setStartTime(requestDto.getStartTime());
        response.setEndTime(requestDto.getEndTime());
        response.setLocationId(requestDto.getLocationId());
        response.setAppointmentStatus(Appointment.AppointmentStatus.PLANNED);
        
        return response;
    }
    
    @Override
    public List<AppointmentResponseDto> getAllAppointments() {
        // Mock 데이터 반환
        List<AppointmentResponseDto> appointments = new ArrayList<>();
        
        AppointmentResponseDto appointment1 = new AppointmentResponseDto();
        appointment1.setAppointmentId("appointment-001");
        appointment1.setHostId("user123");
        appointment1.setTitle("커피챗");
        appointment1.setDescription("팀원들과의 커피챗");
        appointment1.setStartTime(LocalDateTime.now().plusHours(1));
        appointment1.setEndTime(LocalDateTime.now().plusHours(2));
        appointment1.setLocationId("location001");
        appointment1.setAppointmentStatus(Appointment.AppointmentStatus.PLANNED);
        
        appointments.add(appointment1);
        
        return appointments;
    }
    
    @Override
    public AppointmentResponseDto getAppointmentById(String appointmentId) {
        // Mock 데이터 반환
        AppointmentResponseDto appointment = new AppointmentResponseDto();
        appointment.setAppointmentId(appointmentId);
        appointment.setHostId("user123");
        appointment.setTitle("커피챗");
        appointment.setDescription("팀원들과의 커피챗");
        appointment.setStartTime(LocalDateTime.now().plusHours(1));
        appointment.setEndTime(LocalDateTime.now().plusHours(2));
        appointment.setLocationId("location001");
        appointment.setAppointmentStatus(Appointment.AppointmentStatus.PLANNED);
        
        return appointment;
    }
    
    @Override
    public List<AppointmentResponseDto> getAppointmentsByHostId(String hostId) {
        return getAllAppointments(); // Mock 데이터 반환
    }
    
    @Override
    public List<AppointmentResponseDto> getAppointmentsByLocationId(String locationId) {
        return getAllAppointments(); // Mock 데이터 반환
    }
    
    @Override
    public List<AppointmentResponseDto> getAppointmentsByStartTime(LocalDateTime startTime) {
        return getAllAppointments(); // Mock 데이터 반환
    }
    
    @Override
    public List<AppointmentResponseDto> getAppointmentsByEndTime(LocalDateTime endTime) {
        return getAllAppointments(); // Mock 데이터 반환
    }
    
    @Override
    public Appointment.AppointmentStatus getAppointmentStatus(String appointmentId) {
        return Appointment.AppointmentStatus.PLANNED; // Mock 데이터 반환
    }
    
    @Override
    public AppointmentResponseDto updateAppointmentStatus(String appointmentId, AppointmentStatusUpdateDto statusUpdateDto) {
        // Mock 데이터 반환
        AppointmentResponseDto appointment = new AppointmentResponseDto();
        appointment.setAppointmentId(appointmentId);
        appointment.setHostId("user123");
        appointment.setTitle("커피챗");
        appointment.setDescription("팀원들과의 커피챗");
        appointment.setStartTime(LocalDateTime.now().plusHours(1));
        appointment.setEndTime(LocalDateTime.now().plusHours(2));
        appointment.setLocationId("location001");
        appointment.setAppointmentStatus(statusUpdateDto.getAppointmentStatus());
        
        return appointment;
    }
    
    @Override
    public List<AppointmentResponseDto> getAppointmentsWithFilters(String locationId, 
                                                                 Appointment.AppointmentStatus appointmentStatus,
                                                                 LocalDateTime startTime, 
                                                                 LocalDateTime endTime) {
        return getAllAppointments(); // Mock 데이터 반환
    }
    
    @Override
    public void deleteAppointment(String appointmentId) {
        // Mock 구현 - 실제로는 아무것도 하지 않음
    }
}
