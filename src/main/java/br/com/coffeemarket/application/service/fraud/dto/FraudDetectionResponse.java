package br.com.coffeemarket.application.service.fraud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FraudDetectionResponse", description = "Response DTO for fraud detection result")
public class FraudDetectionResponse {
    @Schema(description = "Indicates if the order is considered fraudulent", defaultValue = "false")
    private boolean isFraudulent;
    @Schema(description = "Fraud score (e.g., 0.0 to 1.0)", defaultValue = "0.1")
    private double score;
    @Schema(description = "Reason for the fraud assessment", defaultValue = "No suspicious activity detected")
    private String reason;
}
