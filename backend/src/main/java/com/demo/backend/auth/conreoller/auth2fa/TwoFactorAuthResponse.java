package com.demo.backend.auth.conreoller.auth2fa;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
public class TwoFactorAuthResponse {
    private String secretKey;
    private String authenticatorUrl;
}
