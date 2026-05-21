package com.example.application_e_commerce.payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentContext {
    
    private final Map<String, PaymentStrategy> strategies = new ConcurrentHashMap<>();
    
    @Autowired
    private List<PaymentStrategy> paymentStrategies;
    
    @PostConstruct
    public void init() {
        for (PaymentStrategy strategy : paymentStrategies) {
            strategies.put(strategy.getPaymentMethodName(), strategy);
        }
    }
    
    public PaymentResult executePayment(String paymentMethod, java.math.BigDecimal amount, 
                                        String currency, PaymentDetails details) {
        PaymentStrategy strategy = strategies.get(paymentMethod);
        if (strategy == null) {
            PaymentResult result = new PaymentResult();
            result.setSuccess(false);
            result.setMessage("Mode de paiement non supporté: " + paymentMethod);
            return result;
        }
        return strategy.processPayment(amount, currency, details);
    }
    
    // Méthode pour ajouter dynamiquement de nouveaux modes de paiement
    public void addPaymentStrategy(PaymentStrategy strategy) {
        strategies.put(strategy.getPaymentMethodName(), strategy);
    }
}
