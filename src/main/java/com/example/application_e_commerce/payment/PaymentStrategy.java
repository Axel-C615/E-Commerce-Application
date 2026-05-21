package com.example.application_e_commerce.payment;

import java.math.BigDecimal;

public interface PaymentStrategy {
    
    PaymentResult processPayment(BigDecimal amount, String currency, PaymentDetails details);
    
    String getPaymentMethodName();
    
    boolean supports(String paymentMethod);
}