package com.example.appointment.controller;

import com.example.appointment.dto.*;
import com.example.appointment.entity.Appointment;
import com.example.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 약속 관리 Controller
 */
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    /**
     * 약속 생성
     * POST /appointments
     */
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequestDto requestDto) {
        try {
            // 요청 형식 검증
            if (requestDto == null) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Request body cannot be null"));
            }
            
            if (requestDto.getHostId() == null || requestDto.getHostId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Host ID cannot be null or empty"));
            }
            
            if (requestDto.getTitle() == null || requestDto.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Title cannot be null or empty"));
            }
            
            if (requestDto.getLocationId() == null || requestDto.getLocationId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Location ID cannot be null or empty"));
            }
            
            if (requestDto.getStartTime() == null) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Start time cannot be null"));
            }
            
            if (requestDto.getEndTime() == null) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "End time cannot be null"));
            }
            
            AppointmentResponseDto response = appointmentService.createAppointment(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error creating appointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to create appointment"));
        }
    }
    
    /**
     * 전체 약속 목록 조회
     * GET /appointments
     */
    @GetMapping
    public ResponseEntity<?> getAllAppointments(
            @RequestParam(required = false) String location_id,
            @RequestParam(required = false) String appointment_status,
            @RequestParam(required = false) String start_time,
            @RequestParam(required = false) String end_time) {
        
        try {
            // 필터링 파라미터가 있는 경우
            if (location_id != null || appointment_status != null || start_time != null || end_time != null) {
                return getAppointmentsWithFilters(location_id, appointment_status, start_time, end_time);
            }
            
            List<AppointmentResponseDto> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(appointments);
            
        } catch (Exception e) {
            log.error("Error retrieving appointments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve appointments"));
        }
    }
    
    /**
     * 약속 상세 조회
     * GET /appointments/{appointment_id}
     */
    @GetMapping("/{appointment_id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable("appointment_id") String appointmentId) {
        try {
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Appointment ID cannot be null or empty"));
            }
            
            AppointmentResponseDto appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(appointment);
            
        } catch (Exception e) {
            log.error("Error retrieving appointment with ID: {}", appointmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve appointment"));
        }
    }
    
    /**
     * 호스트의 약속 목록 조회
     * GET /appointments/host/{host_id}
     */
    @GetMapping("/host/{host_id}")
    public ResponseEntity<?> getAppointmentsByHostId(@PathVariable("host_id") String hostId) {
        try {
            if (hostId == null || hostId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Host ID cannot be null or empty"));
            }
            
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByHostId(hostId);
            return ResponseEntity.ok(appointments);
            
        } catch (Exception e) {
            log.error("Error retrieving appointments for host: {}", hostId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve appointments for host"));
        }
    }
    
    /**
     * 위치별 약속 목록 조회
     * GET /appointments/location/{location_id}
     */
    @GetMapping("/location/{location_id}")
    public ResponseEntity<?> getAppointmentsByLocationId(@PathVariable("location_id") String locationId) {
        try {
            if (locationId == null || locationId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Location ID cannot be null or empty"));
            }
            
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByLocationId(locationId);
            return ResponseEntity.ok(appointments);
            
        } catch (Exception e) {
            log.error("Error retrieving appointments for location: {}", locationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve appointments for location"));
        }
    }
    
    /**
     * 시작 시간별 약속 목록 조회
     * GET /appointments/start-time/{start_time}
     */
    @GetMapping("/start-time/{start_time}")
    public ResponseEntity<?> getAppointmentsByStartTime(@PathVariable("start_time") String startTimeStr) {
        try {
            if (startTimeStr == null || startTimeStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Start time cannot be null or empty"));
            }
            
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByStartTime(startTime);
            return ResponseEntity.ok(appointments);
            
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("InvalidRequest", "Invalid start time format"));
        } catch (Exception e) {
            log.error("Error retrieving appointments for start time: {}", startTimeStr, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve appointments for start time"));
        }
    }
    
    /**
     * 종료 시간별 약속 목록 조회
     * GET /appointments/end-time/{end_time}
     */
    @GetMapping("/end-time/{end_time}")
    public ResponseEntity<?> getAppointmentsByEndTime(@PathVariable("end_time") String endTimeStr) {
        try {
            if (endTimeStr == null || endTimeStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "End time cannot be null or empty"));
            }
            
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByEndTime(endTime);
            return ResponseEntity.ok(appointments);
            
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("InvalidRequest", "Invalid end time format"));
        } catch (Exception e) {
            log.error("Error retrieving appointments for end time: {}", endTimeStr, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve appointments for end time"));
        }
    }
    
    /**
     * 약속 상태 조회
     * GET /appointments/{appointment_id}/status
     */
    @GetMapping("/{appointment_id}/status")
    public ResponseEntity<?> getAppointmentStatus(@PathVariable("appointment_id") String appointmentId) {
        try {
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Appointment ID cannot be null or empty"));
            }
            
            Appointment.AppointmentStatus status = appointmentService.getAppointmentStatus(appointmentId);
            if (status == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            log.error("Error retrieving appointment status for ID: {}", appointmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve appointment status"));
        }
    }
    
    /**
     * 약속 상태 변경
     * PUT /appointments/{appointment_id}/status
     */
    @PutMapping("/{appointment_id}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable("appointment_id") String appointmentId,
            @RequestBody AppointmentStatusUpdateDto statusUpdateDto) {
        
        try {
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Appointment ID cannot be null or empty"));
            }
            
            if (statusUpdateDto == null) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Status update request cannot be null"));
            }
            
            if (statusUpdateDto.getAppointmentStatus() == null) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Appointment status cannot be null"));
            }
            
            AppointmentResponseDto response = appointmentService.updateAppointmentStatus(appointmentId, statusUpdateDto);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating appointment status for ID: {}", appointmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to update appointment status"));
        }
    }
    
    /**
     * 약속 목록 조회 (필터링)
     * GET /appointments?location_id={location_id}&appointment_status={appointment_status}&start_time={start_time}&end_time={end_time}
     */
    private ResponseEntity<?> getAppointmentsWithFilters(
            String locationId, 
            String appointmentStatus, 
            String startTimeStr, 
            String endTimeStr) {
        
        try {
            Appointment.AppointmentStatus status = null;
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            
            // 상태 파싱
            if (appointmentStatus != null && !appointmentStatus.trim().isEmpty()) {
                try {
                    status = Appointment.AppointmentStatus.valueOf(appointmentStatus.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(new ErrorResponse("InvalidRequest", "Invalid appointment status"));
                }
            }
            
            // 시작 시간 파싱
            if (startTimeStr != null && !startTimeStr.trim().isEmpty()) {
                try {
                    startTime = LocalDateTime.parse(startTimeStr);
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest()
                        .body(new ErrorResponse("InvalidRequest", "Invalid start time format"));
                }
            }
            
            // 종료 시간 파싱
            if (endTimeStr != null && !endTimeStr.trim().isEmpty()) {
                try {
                    endTime = LocalDateTime.parse(endTimeStr);
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest()
                        .body(new ErrorResponse("InvalidRequest", "Invalid end time format"));
                }
            }
            
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsWithFilters(
                locationId, status, startTime, endTime);
            
            return ResponseEntity.ok(appointments);
            
        } catch (Exception e) {
            log.error("Error retrieving filtered appointments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to retrieve filtered appointments"));
        }
    }
    
    /**
     * 약속 삭제
     * DELETE /appointments/{appointment_id}
     */
    @DeleteMapping("/{appointment_id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable("appointment_id") String appointmentId) {
        try {
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("InvalidRequest", "Appointment ID cannot be null or empty"));
            }
            
            appointmentService.deleteAppointment(appointmentId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Error deleting appointment with ID: {}", appointmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("InternalError", "Failed to delete appointment"));
        }
    }
}
