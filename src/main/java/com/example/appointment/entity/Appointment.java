package com.example.appointment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 약속 엔티티 - MySQL 최적화
 */
@Entity
@Table(name = "appointment", 
       indexes = {
           @Index(name = "idx_host_id", columnList = "host_id"),
           @Index(name = "idx_location_id", columnList = "location_id"),
           @Index(name = "idx_start_time", columnList = "start_time"),
           @Index(name = "idx_end_time", columnList = "end_time"),
           @Index(name = "idx_appointment_status", columnList = "appointment_status"),
           @Index(name = "idx_host_time", columnList = "host_id, start_time, end_time")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    @Id
    @Column(name = "appointment_id", length = 100, nullable = false)
    private String appointmentId;
    
    @Column(name = "host_id", length = 100, nullable = false)
    private String hostId;
    
    @Column(name = "title", length = 200, nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;
    
    @Column(name = "start_time", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime endTime;
    
    @Column(name = "location_id", length = 100, nullable = false)
    private String locationId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", length = 20, nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PLANNED'")
    private AppointmentStatus appointmentStatus;
    
    // 감사(Audit) 필드 추가
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private LocalDateTime updatedAt;
    
    /**
     * 약속 상태 열거형
     */
    public enum AppointmentStatus {
        PLANNED,    // 계획됨
        ONGOING,    // 진행중
        DONE,       // 완료
        CANCELLED   // 취소
    }
    
    /**
     * 엔티티 생성 전 기본값 설정
     */
    @PrePersist
    protected void onCreate() {
        if (appointmentStatus == null) {
            appointmentStatus = AppointmentStatus.PLANNED;
        }
    }
}
