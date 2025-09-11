package com.example.appointment.client;

import com.example.appointment.dto.ApiResponse;
import com.example.appointment.dto.GuestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Guest 서비스 클라이언트
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GuestServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.guest.url}")
    private String guestServiceUrl;
    
    @Value("${services.guest.api-key}")
    private String apiKey;
    
    /**
     * 약속 ID로 Guest 목록 조회
     */
    public List<GuestResponse> getGuestsByAppointmentId(String appointmentId) {
        log.info("GuestService에서 Guest 정보 조회 시작 - appointmentId: {}", appointmentId);
        
        WebClient webClient = webClientBuilder
                .baseUrl(guestServiceUrl)
                .defaultHeader("X-API-Key", apiKey)
                .defaultHeader("User-Agent", "appointment-service/1.0")
                .build();
        
        try {
            ApiResponse<List<GuestResponse>> apiResponse = webClient
                    .get()
                    .uri("/guests/appointment/{appointmentId}", appointmentId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<GuestResponse>>>() {})
                    .block();
            
            List<GuestResponse> guestResponses = apiResponse != null ? apiResponse.getData() : List.of();
            
            log.info("GuestService에서 Guest 정보 조회 완료 - appointmentId: {}, guestCount: {}", 
                    appointmentId, guestResponses.size());
            
            return guestResponses;
            
        } catch (Exception e) {
            log.error("GuestService에서 Guest 정보 조회 실패 - appointmentId: {}", appointmentId, e);
            return List.of(); // 빈 리스트 반환
        }
    }
}
