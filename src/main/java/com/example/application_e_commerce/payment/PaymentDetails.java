package com.example.application_e_commerce.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetails {
    
    // ========== Informations génériques ==========
    private String paymentMethod;
    private java.math.BigDecimal amount;
    private String currency;
    private String description;
    private String customerEmail;
    private String customerName;
    
    // ========== Informations carte bancaire ==========
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String cardType;
    private String cardHolderName;
    
    // ========== Informations PayPal ==========
    private String paypalEmail;
    private String returnUrl;
    private String cancelUrl;
    private String paypalOrderId;
    private String paypalPaymentId;
    private String paypalPayerId;
    
    // ========== Méthodes utilitaires ==========
    public boolean isCardPayment() {
        return cardNumber != null && !cardNumber.isEmpty() 
            && cardExpiry != null && !cardExpiry.isEmpty()
            && cardCvv != null && !cardCvv.isEmpty();
    }
    
    public boolean isPaypalPayment() {
        return paypalEmail != null && !paypalEmail.isEmpty();
    }
    
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}