package br.com.coffeemarket.application.service.fraud;

import br.com.coffeemarket.application.service.fraud.dto.FraudDetectionRequest;
import br.com.coffeemarket.application.service.fraud.dto.FraudDetectionResponse;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.Random;

@ApplicationScoped
public class FraudDetectionService {

    private final Logger log;

    public FraudDetectionService(Logger log) {
        this.log = log;
    }

    private final Random random = new Random();

    public Uni<FraudDetectionResponse> checkFraud(FraudDetectionRequest request) {
        log.info("Checking for fraud for order: " + request + " on thread: " + Thread.currentThread().getName());

        // Simulate a fraud check (rule-based)
        // For demonstration, let's say orders with totalAmount > 500 or customerName "Fraudster" are fraudulent
        boolean isFraudulent = request.getTotalAmount().compareTo(BigDecimal.valueOf(500)) > 0 ||
                               request.getCustomerName().equalsIgnoreCase("Fraudster");

        double score = isFraudulent ? 0.9 : random.nextDouble() * 0.3; // High score for fraud, low for non-fraud
        String reason = isFraudulent ? "High total amount or suspicious customer name" : "No suspicious activity detected";

        return Uni.createFrom().item(new FraudDetectionResponse(isFraudulent, score, reason))
                .onItem().invoke(response -> log.info("Fraud check result: " + response + " on thread: " + Thread.currentThread().getName()));
    }
}
