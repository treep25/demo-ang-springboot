package com.demo.backend.auth;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RegRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String repeatPassword;
}
