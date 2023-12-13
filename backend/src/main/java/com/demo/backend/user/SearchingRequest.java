package com.demo.backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchingRequest {
    private String firstName;
    private String lastName;
}
