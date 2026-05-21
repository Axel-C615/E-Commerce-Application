package com.example.application_e_commerce.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResult {
    
    private boolean success;
    private String transactionId;
    private String message;
    private String paymentUrl;
    private String errorCode;
    private java.math.BigDecimal amountCharged;
    private String currency;
    private LocalDateTime timestamp;
    private String additionalDetails;
    
    public static PaymentResult success(String transactionId, String message) {
        return PaymentResult.builder()
            .success(true)
            .transactionId(transactionId)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    public static PaymentResult failure(String errorCode, String message) {
        return PaymentResult.builder()
            .success(false)
            .errorCode(errorCode)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    public static PaymentResult redirect(String paymentUrl, String transactionId) {
        return PaymentResult.builder()
            .success(true)
            .paymentUrl(paymentUrl)
            .transactionId(transactionId)
            .message("Redirection vers la plateforme de paiement")
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    public boolean requiresRedirect() {
        return success && paymentUrl != null && !paymentUrl.isEmpty();
    }
    
    public boolean isPending() {
        return !success && "PENDING".equals(errorCode);
    }
}