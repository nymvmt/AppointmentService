package com.example.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Guest 정보 DTO (프론트 요청용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestInfo {
    
    private String guestId;
    private String userId;
    private String username;
    private String nickname;
}

