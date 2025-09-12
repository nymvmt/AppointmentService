package com.example.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Guest 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse {
    
    private String guestId;
    private String appointmentId;
    private String userId;
    private String username;
    private String nickname;
}

