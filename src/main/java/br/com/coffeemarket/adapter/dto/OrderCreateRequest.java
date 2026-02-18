package br.com.coffeemarket.adapter.dto;

import br.com.coffeemarket.application.domain.enuns.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OrderCreateRequest", description = "DTO for creating a new order")
public class OrderCreateRequest {
    @Schema(description = "Date and time of the order", defaultValue = "2023-10-27T10:00:00Z", implementation = String.class, format = "date-time")
    private OffsetDateTime orderDate;
    @Schema(description = "Name of the customer placing the order", defaultValue = "John Doe")
    private String customerName;
    @Schema(description = "Total amount of the order", defaultValue = "123.45")
    private BigDecimal totalAmount;
    @Schema(description = "Currency of the order (e.g., BRL, USD)", defaultValue = "BRL")
    private String currency;
    @Schema(description = "Current status of the order", defaultValue = "CREATED", enumeration = {"CREATED", "PENDING", "FRAUD_DETECTED"})
    private OrderStatus status;
}