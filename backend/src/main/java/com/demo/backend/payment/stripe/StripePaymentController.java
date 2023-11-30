package com.demo.backend.payment.stripe;

import com.demo.backend.payment.StripePaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class StripePaymentController {

    @Value("${stripe.secret-key}")
    private String secretKey;
    private final StripePaymentService stripeService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @PostMapping("/stripe/create-payment-intent")
//    @RequestBody StripeRequest request
    public ResponseEntity<?> createPaymentIntent() throws StripeException {
//        try {
//            Map<String, Object> params = new HashMap<>();
//            params.put("amount", request.getAmount());
//            params.put("currency", "usd");
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//            Map<String, Object> responseData = new HashMap<>();
//            responseData.put("clientSecret", paymentIntent.getClientSecret());
//
//            return ResponseEntity.ok(responseData);
//        } catch (StripeException e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body(null);
//        }

        List<Object> lit = new ArrayList<>();
        lit.add("card");
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 2000);
        params.put("currency", "usd");
        params.put("payment_method_types", lit);
        params.put("metadata", Map.of("orderId", "abc12345"));

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return ResponseEntity.ok(paymentIntent.getClientSecret());
    }
}
