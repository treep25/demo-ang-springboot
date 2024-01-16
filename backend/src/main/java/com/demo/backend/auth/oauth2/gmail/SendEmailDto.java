package com.demo.backend.auth.oauth2.gmail;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
public class SendEmailDto {

    private String to;
    private String subject;
    private String body;
}
