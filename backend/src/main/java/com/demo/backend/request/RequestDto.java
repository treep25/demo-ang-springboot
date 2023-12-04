package com.demo.backend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDto {
    private String email;
    private String issue;
    private RequestStatus status;
}






















