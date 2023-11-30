package com.demo.backend.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentService {
    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Charge charge()
            throws StripeException {
        ChargeRequest chargeRequest = new ChargeRequest();
        chargeRequest.setDescription("Oops");
        chargeRequest.setAmount(10000);
        chargeRequest.setCurrency(ChargeRequest.Currency.USD);
        chargeRequest.setStripeEmail("jenny.rosen@gmail.com");
        chargeRequest.setStripeToken("pk_test_51OHikCAiCn0BUr4JPSqR5FDOhT5nGSiIvCkqFv5urEAAg12ymu317v7gAkfbFAPfK0D10L8AhnJzzdScYZOFP5WX00aITNen2Z");
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("source", secretKey);
        return Charge.create(chargeParams);
    }
}
