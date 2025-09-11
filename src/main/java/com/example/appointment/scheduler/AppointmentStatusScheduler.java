package com.example.appointment.scheduler;

import com.example.appointment.entity.Appointment;
import com.example.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 약속 상태 실시간 변경 스케줄러
 * - PLANNED → ONGOING: 시작 시간 도달 시
 * - ONGOING → DONE: 종료 시간 도달 시
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentStatusScheduler {
    
    private final AppointmentRepository appointmentRepository;
    
    /**
     * 매 1분마다 약속 상태를 실시간으로 확인하고 업데이트
     */
    @Scheduled(fixedRate = 60000) // 60초(1분)마다 실행
    @Transactional
    public void updateAppointmentStatuses() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            log.debug("실시간 약속 상태 체크 시작: {}", now);
            
            // 1. PLANNED → ONGOING 변경
            updatePlannedToOngoing(now);
            
            // 2. ONGOING → DONE 변경  
            updateOngoingToDone(now);
            
            // 3. PLANNED에서 바로 DONE으로 변경 (늦게 확인된 경우)
            updatePlannedToDone(now);
            
            log.debug("실시간 약속 상태 체크 완료");
        } catch (Exception e) {
            log.error("약속 상태 업데이트 중 오류 발생", e);
        }
    }
    
    /**
     * PLANNED → ONGOING 상태 변경
     * 현재 시간이 시작 시간 이후이고 종료 시간 이전인 약속들
     */
    private void updatePlannedToOngoing(LocalDateTime now) {
        List<Appointment> appointmentsToStart = appointmentRepository.findAppointmentsToStartNow(now);
        
        if (!appointmentsToStart.isEmpty()) {
            // 배치 업데이트: 메모리에서 상태 변경 후 한 번에 저장
            appointmentsToStart.forEach(appointment -> 
                appointment.setAppointmentStatus(Appointment.AppointmentStatus.ONGOING));
            appointmentRepository.saveAll(appointmentsToStart);
            
            // 로깅
            appointmentsToStart.forEach(appointment -> 
                log.info("실시간 상태 변경: {} - PLANNED → ONGOING (시작: {})", 
                        appointment.getAppointmentId(), 
                        appointment.getStartTime()));
            
            log.info("PLANNED → ONGOING 배치 변경 완료: {}개 약속", appointmentsToStart.size());
        }
    }
    
    /**
     * ONGOING → DONE 상태 변경
     * 현재 시간이 종료 시간 이후인 약속들
     */
    private void updateOngoingToDone(LocalDateTime now) {
        List<Appointment> appointmentsToEnd = appointmentRepository.findAppointmentsToEndNow(now);
        
        if (!appointmentsToEnd.isEmpty()) {
            // 배치 업데이트: 메모리에서 상태 변경 후 한 번에 저장
            appointmentsToEnd.forEach(appointment -> 
                appointment.setAppointmentStatus(Appointment.AppointmentStatus.DONE));
            appointmentRepository.saveAll(appointmentsToEnd);
            
            // 로깅
            appointmentsToEnd.forEach(appointment -> 
                log.info("실시간 상태 변경: {} - ONGOING → DONE (종료: {})", 
                        appointment.getAppointmentId(), 
                        appointment.getEndTime()));
            
            log.info("ONGOING → DONE 배치 변경 완료: {}개 약속", appointmentsToEnd.size());
        }
    }
    
    /**
     * PLANNED → DONE 상태 변경 (늦게 확인된 경우)
     * PLANNED 상태인데 이미 종료 시간이 지난 약속들
     */
    private void updatePlannedToDone(LocalDateTime now) {
        List<Appointment> plannedPastEndTime = appointmentRepository.findPlannedAppointmentsPastEndTime(now);
        
        if (!plannedPastEndTime.isEmpty()) {
            // 배치 업데이트: 메모리에서 상태 변경 후 한 번에 저장
            plannedPastEndTime.forEach(appointment -> 
                appointment.setAppointmentStatus(Appointment.AppointmentStatus.DONE));
            appointmentRepository.saveAll(plannedPastEndTime);
            
            // 로깅
            plannedPastEndTime.forEach(appointment -> 
                log.info("실시간 상태 변경: {} - PLANNED → DONE (종료시간 지남: {})", 
                        appointment.getAppointmentId(), 
                        appointment.getEndTime()));
            
            log.info("PLANNED → DONE 배치 변경 완료: {}개 약속 (늦은 처리)", plannedPastEndTime.size());
        }
    }
    
    /**
     * 수동 실행용 메서드 (테스트/관리용)
     */
    public void manualStatusUpdate() {
        log.info("수동 약속 상태 업데이트 실행");
        updateAppointmentStatuses();
    }
}
