package br.com.coffeemarket.application.service.fraud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FraudDetectionRequest", description = "Request DTO for fraud detection")
public class FraudDetectionRequest {
    @Schema(description = "Date and time of the order", defaultValue = "2023-10-27T10:00:00Z", implementation = String.class, format = "date-time")
    private OffsetDateTime orderDate;
    @Schema(description = "Name of the customer placing the order", defaultValue = "John Doe")
    private String customerName;
    @Schema(description = "Total amount of the order", defaultValue = "123.45")
    private BigDecimal totalAmount;
    @Schema(description = "Currency of the order (e.g., BRL, USD)", defaultValue = "BRL")
    private String currency;
    // Add other relevant fields for fraud detection from OrderCreateRequest if needed
}
