package com.example.appointment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 약속 엔티티
 */
@Entity
@Table(name = "appointment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    @Id
    @Column(name = "appointment_id", length = 100)
    private String appointmentId;
    
    @Column(name = "host_id", length = 100, nullable = false)
    private String hostId;
    
    @Column(name = "title", length = 200, nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "location_id", length = 100, nullable = false)
    private String locationId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", length = 20, nullable = false)
    private AppointmentStatus appointmentStatus;
    
    /**
     * 약속 상태 열거형
     */
    public enum AppointmentStatus {
        PLANNED,    // 계획됨
        ONGOING,    // 진행중
        DONE,       // 완료
        CANCELLED   // 취소
    }
}
