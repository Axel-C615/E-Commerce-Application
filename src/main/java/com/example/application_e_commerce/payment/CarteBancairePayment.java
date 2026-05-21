package com.example.application_e_commerce.payment;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CarteBancairePayment implements PaymentStrategy {
    
    @Override
    public PaymentResult processPayment(BigDecimal amount, String currency, PaymentDetails details) {
        // Simulation du paiement par carte bancaire
        // Dans un cas réel, intégrer Stripe ou autre API
        
        PaymentResult result = new PaymentResult();
        
        try {
            // Validation simple de la carte
            if (validateCard(details)) {
                result.setSuccess(true);
                result.setTransactionId("CARD_" + UUID.randomUUID().toString());
                result.setMessage("Paiement par carte bancaire effectué avec succès");
            } else {
                result.setSuccess(false);
                result.setMessage("Carte bancaire invalide");
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Erreur lors du paiement: " + e.getMessage());
        }
        
        return result;
    }
    
    private boolean validateCard(PaymentDetails details) {
        // Logique de validation simple
        // Dans la réalité, appeler une API de paiement
        return details.getCardNumber() != null && 
               details.getCardNumber().length() >= 13 &&
               details.getCardCvv() != null && 
               details.getCardCvv().length() == 3;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "CARTE_BANCAIRE";
    }
    
    @Override
    public boolean supports(String paymentMethod) {
        return "CARTE_BANCAIRE".equalsIgnoreCase(paymentMethod);
    }
}
