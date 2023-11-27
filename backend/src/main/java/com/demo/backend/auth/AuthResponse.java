package com.demo.backend.auth;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
