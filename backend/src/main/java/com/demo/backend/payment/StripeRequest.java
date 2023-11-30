package com.demo.backend.payment;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StripeRequest {
    private String token;
    private long amount;
}
