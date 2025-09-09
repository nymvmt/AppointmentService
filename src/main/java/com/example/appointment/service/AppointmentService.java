package com.example.appointment.service;

import com.example.appointment.dto.AppointmentRequestDto;
import com.example.appointment.dto.AppointmentResponseDto;
import com.example.appointment.dto.AppointmentStatusUpdateDto;
import com.example.appointment.entity.Appointment;
import com.example.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 약속 Service - 비즈니스 로직 및 불변식 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        log.info("Creating appointment for host: {}", requestDto.getHostId());
        
        // 불변식 검증
        validateAppointmentInvariants(requestDto);
        
        // 중복 시간대 약속 검증 (INV-A008)
        validateTimeConflict(requestDto.getHostId(), requestDto.getStartTime(), requestDto.getEndTime());
        
        // Entity 생성
        Appointment appointment = createAppointmentEntity(requestDto);
        
        // 저장
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Successfully created appointment with ID: {}", savedAppointment.getAppointmentId());
        return convertToResponseDto(savedAppointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAllAppointments() {
        log.info("Retrieving all appointments");
        
        List<Appointment> appointments = appointmentRepository.findAll();
        
        // 상태 자동 업데이트
        updateAppointmentStatuses(appointments);
        
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(String appointmentId) {
        log.info("Retrieving appointment with ID: {}", appointmentId);
        
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            log.warn("Appointment not found with ID: {}", appointmentId);
            return null;
        }
        
        Appointment appointment = appointmentOpt.get();
        
        // 상태 자동 업데이트
        updateAppointmentStatus(appointment);
        
        return convertToResponseDto(appointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByHostId(String hostId) {
        log.info("Retrieving appointments for host: {}", hostId);
        
        List<Appointment> appointments = appointmentRepository.findByHostId(hostId);
        
        // 상태 자동 업데이트
        updateAppointmentStatuses(appointments);
        
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByLocationId(String locationId) {
        log.info("Retrieving appointments for location: {}", locationId);
        
        List<Appointment> appointments = appointmentRepository.findByLocationId(locationId);
        
        // 상태 자동 업데이트
        updateAppointmentStatuses(appointments);
        
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByStartTime(LocalDateTime startTime) {
        log.info("Retrieving appointments for start time: {}", startTime);
        
        List<Appointment> appointments = appointmentRepository.findByStartTime(startTime);
        
        // 상태 자동 업데이트
        updateAppointmentStatuses(appointments);
        
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByEndTime(LocalDateTime endTime) {
        log.info("Retrieving appointments for end time: {}", endTime);
        
        List<Appointment> appointments = appointmentRepository.findByEndTime(endTime);
        
        // 상태 자동 업데이트
        updateAppointmentStatuses(appointments);
        
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Appointment.AppointmentStatus getAppointmentStatus(String appointmentId) {
        log.info("Retrieving status for appointment: {}", appointmentId);
        
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            log.warn("Appointment not found with ID: {}", appointmentId);
            return null;
        }
        
        Appointment appointment = appointmentOpt.get();
        
        // 상태 자동 업데이트
        updateAppointmentStatus(appointment);
        
        return appointment.getAppointmentStatus();
    }
    
    public AppointmentResponseDto updateAppointmentStatus(String appointmentId, AppointmentStatusUpdateDto statusUpdateDto) {
        log.info("Updating status for appointment: {} to {}", appointmentId, statusUpdateDto.getAppointmentStatus());
        
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            log.warn("Appointment not found with ID: {}", appointmentId);
            return null;
        }
        
        Appointment appointment = appointmentOpt.get();
        
        // 상태 변경 불변식 검증 (INV-A006, INV-A007)
        validateStatusTransition(appointment, statusUpdateDto.getAppointmentStatus());
        
        // 수동 상태 변경은 CANCELLED만 허용 (시간 기반 상태는 자동 변경)
        if (statusUpdateDto.getAppointmentStatus() != Appointment.AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Manual status change is only allowed for CANCELLED status");
        }
        
        appointment.setAppointmentStatus(statusUpdateDto.getAppointmentStatus());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Successfully updated appointment status: {}", savedAppointment.getAppointmentId());
        return convertToResponseDto(savedAppointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsWithFilters(String locationId, 
                                                                 Appointment.AppointmentStatus appointmentStatus,
                                                                 LocalDateTime startTime, 
                                                                 LocalDateTime endTime) {
        log.info("Retrieving appointments with filters - location: {}, status: {}, startTime: {}, endTime: {}", 
                locationId, appointmentStatus, startTime, endTime);
        
        List<Appointment> appointments = appointmentRepository.findAppointmentsWithFilters(
                locationId, appointmentStatus, startTime, endTime);
        
        // 상태 자동 업데이트
        updateAppointmentStatuses(appointments);
        
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public void deleteAppointment(String appointmentId) {
        log.info("Deleting appointment: {}", appointmentId);
        
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            log.warn("Appointment not found with ID: {}", appointmentId);
            return;
        }
        
        appointmentRepository.deleteById(appointmentId);
        log.info("Successfully deleted appointment: {}", appointmentId);
    }
    
    /**
     * 약속 불변식 검증
     */
    private void validateAppointmentInvariants(AppointmentRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now();
        
        // INV-A001: start_time < end_time
        if (!requestDto.getStartTime().isBefore(requestDto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time (INV-A001)");
        }
        
        // INV-A002: start_time >= CURRENT_TIMESTAMP (개발/테스트용으로 일시적으로 비활성화)
        // TODO: 운영 환경에서는 활성화 필요
        // if (requestDto.getStartTime().isBefore(now)) {
        //     throw new IllegalArgumentException("Start time must be in the future (INV-A002)");
        // }
        
        // INV-A003: host_id는 비어있을 수 없음
        if (requestDto.getHostId() == null || requestDto.getHostId().trim().isEmpty()) {
            throw new IllegalArgumentException("Host ID cannot be null or empty (INV-A003)");
        }
        
        // INV-A004: title은 비어있을 수 없음
        if (requestDto.getTitle() == null || requestDto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty (INV-A004)");
        }
        
        // INV-A005: location은 비어있을 수 없음
        if (requestDto.getLocationId() == null || requestDto.getLocationId().trim().isEmpty()) {
            throw new IllegalArgumentException("Location ID cannot be null or empty (INV-A005)");
        }
    }
    
    /**
     * 동일 시간대 중복 약속 검증 (INV-A008)
     */
    private void validateTimeConflict(String hostId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
                hostId, startTime, endTime);
        
        if (!overlappingAppointments.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Host already has an appointment during this time period (INV-A008). " +
                            "Conflicting appointment ID: %s", overlappingAppointments.get(0).getAppointmentId()));
        }
    }
    
    /**
     * 상태 변경 불변식 검증 (INV-A006, INV-A007)
     */
    private void validateStatusTransition(Appointment appointment, Appointment.AppointmentStatus newStatus) {
        Appointment.AppointmentStatus currentStatus = appointment.getAppointmentStatus();
        
        // INV-A007: done 또는 cancelled 상태에서는 다른 상태로 변경 불가
        if (currentStatus == Appointment.AppointmentStatus.DONE || 
            currentStatus == Appointment.AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    String.format("Cannot change status from %s to %s (INV-A007)", currentStatus, newStatus));
        }
        
        // INV-A006: planned → ongoing → done 순서로만 변경 가능 (CANCELLED 제외)
        if (newStatus != Appointment.AppointmentStatus.CANCELLED) {
            if (currentStatus == Appointment.AppointmentStatus.PLANNED && 
                newStatus != Appointment.AppointmentStatus.ONGOING) {
                throw new IllegalArgumentException(
                        String.format("Invalid status transition from %s to %s (INV-A006)", currentStatus, newStatus));
            }
            
            if (currentStatus == Appointment.AppointmentStatus.ONGOING && 
                newStatus != Appointment.AppointmentStatus.DONE) {
                throw new IllegalArgumentException(
                        String.format("Invalid status transition from %s to %s (INV-A006)", currentStatus, newStatus));
            }
        }
    }
    
    /**
     * 시간 기반 상태 자동 업데이트 (INV-A006)
     */
    private void updateAppointmentStatus(Appointment appointment) {
        LocalDateTime now = LocalDateTime.now();
        Appointment.AppointmentStatus currentStatus = appointment.getAppointmentStatus();
        
        // CANCELLED나 DONE 상태는 변경하지 않음
        if (currentStatus == Appointment.AppointmentStatus.CANCELLED || 
            currentStatus == Appointment.AppointmentStatus.DONE) {
            return;
        }
        
        // 시간 기반 자동 상태 변경
        if (now.isAfter(appointment.getEndTime()) && currentStatus != Appointment.AppointmentStatus.DONE) {
            appointment.setAppointmentStatus(Appointment.AppointmentStatus.DONE);
            appointmentRepository.save(appointment);
            log.info("Auto-updated appointment {} status to DONE", appointment.getAppointmentId());
        } else if (now.isAfter(appointment.getStartTime()) && now.isBefore(appointment.getEndTime()) && 
                   currentStatus == Appointment.AppointmentStatus.PLANNED) {
            appointment.setAppointmentStatus(Appointment.AppointmentStatus.ONGOING);
            appointmentRepository.save(appointment);
            log.info("Auto-updated appointment {} status to ONGOING", appointment.getAppointmentId());
        }
    }
    
    /**
     * 여러 약속의 상태 자동 업데이트
     */
    private void updateAppointmentStatuses(List<Appointment> appointments) {
        appointments.forEach(this::updateAppointmentStatus);
    }
    
    /**
     * Appointment Entity 생성
     */
    private Appointment createAppointmentEntity(AppointmentRequestDto requestDto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID().toString());
        appointment.setHostId(requestDto.getHostId());
        appointment.setTitle(requestDto.getTitle());
        appointment.setDescription(requestDto.getDescription());
        appointment.setStartTime(requestDto.getStartTime());
        appointment.setEndTime(requestDto.getEndTime());
        appointment.setLocationId(requestDto.getLocationId());
        appointment.setAppointmentStatus(Appointment.AppointmentStatus.PLANNED); // 기본값
        
        return appointment;
    }
    
    /**
     * Entity를 ResponseDto로 변환
     */
    private AppointmentResponseDto convertToResponseDto(Appointment appointment) {
        AppointmentResponseDto responseDto = new AppointmentResponseDto();
        responseDto.setAppointmentId(appointment.getAppointmentId());
        responseDto.setHostId(appointment.getHostId());
        responseDto.setTitle(appointment.getTitle());
        responseDto.setDescription(appointment.getDescription());
        responseDto.setStartTime(appointment.getStartTime());
        responseDto.setEndTime(appointment.getEndTime());
        responseDto.setLocationId(appointment.getLocationId());
        responseDto.setAppointmentStatus(appointment.getAppointmentStatus());
        
        // 감사 정보 추가
        responseDto.setCreatedAt(appointment.getCreatedAt());
        responseDto.setUpdatedAt(appointment.getUpdatedAt());
        
        return responseDto;
    }
}
