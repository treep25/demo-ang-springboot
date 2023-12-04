package com.demo.backend.payment.stripe;

import com.demo.backend.payment.StripePaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @PostMapping("/stripe   ")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, String> payload) throws StripeException {
        try {
            Stripe.apiKey = secretKey;

            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", 1000);
            chargeParams.put("currency", "usd");
            chargeParams.put("source", payload.get("token"));

            Charge charge = Charge.create(chargeParams);

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error");
        }
    }
}
