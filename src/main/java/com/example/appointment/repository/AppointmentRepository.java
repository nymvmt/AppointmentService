package com.example.appointment.repository;

import com.example.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 약속 Repository 인터페이스 (Stub)
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    
    // 호스트별 약속 목록 조회
    List<Appointment> findByHostId(String hostId);
    
    // 위치별 약속 목록 조회
    List<Appointment> findByLocationId(String locationId);
    
    // 시작 시간별 약속 목록 조회
    List<Appointment> findByStartTime(LocalDateTime startTime);
    
    // 종료 시간별 약속 목록 조회
    List<Appointment> findByEndTime(LocalDateTime endTime);
    
    // 상태별 약속 목록 조회
    List<Appointment> findByAppointmentStatus(Appointment.AppointmentStatus status);
    
    // 복합 조건으로 약속 목록 조회 (필터링)
    @Query("SELECT a FROM Appointment a WHERE " +
           "(:locationId IS NULL OR a.locationId = :locationId) AND " +
           "(:appointmentStatus IS NULL OR a.appointmentStatus = :appointmentStatus) AND " +
           "(:startTime IS NULL OR a.startTime >= :startTime) AND " +
           "(:endTime IS NULL OR a.endTime <= :endTime)")
    List<Appointment> findAppointmentsWithFilters(
            @Param("locationId") String locationId,
            @Param("appointmentStatus") Appointment.AppointmentStatus appointmentStatus,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    // 호스트의 특정 시간대 중복 약속 확인
    @Query("SELECT a FROM Appointment a WHERE a.hostId = :hostId AND " +
           "((a.startTime <= :startTime AND a.endTime > :startTime) OR " +
           "(a.startTime < :endTime AND a.endTime >= :endTime) OR " +
           "(a.startTime >= :startTime AND a.endTime <= :endTime))")
    List<Appointment> findOverlappingAppointments(
            @Param("hostId") String hostId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
