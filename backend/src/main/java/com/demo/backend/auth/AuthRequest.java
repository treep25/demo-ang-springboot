package com.demo.backend.auth;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class AuthRequest {
    private String email;
    private String password;
}
