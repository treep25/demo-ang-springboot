package com.demo.backend.support.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDto {
    private String content;
    private String recipient;
    private List<String> recipients;
}
