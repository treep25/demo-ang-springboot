package com.demo.backend.auth.oauth2.gmail;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
public class ByParamSearchingDto {
    private String bySender;
    private String bySubject;
    private String byContentSearchTerm;
}
