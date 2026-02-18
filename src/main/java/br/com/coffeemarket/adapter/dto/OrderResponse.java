package br.com.coffeemarket.adapter.dto;

import br.com.coffeemarket.application.domain.entity.Order;
import br.com.coffeemarket.application.domain.enuns.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OrderResponse", description = "DTO for order response")
public class OrderResponse {
    @Schema(description = "Unique identifier of the order", defaultValue = "a1b2c3d4-e5f6-7890-1234-567890abcdef", implementation = String.class, format = "uuid")
    private UUID id;
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
    @Schema(description = "Timestamp when the order was created", defaultValue = "2023-10-27T09:00:00Z", implementation = String.class, format = "date-time")
    private OffsetDateTime createdAt;
    @Schema(description = "Timestamp when the order was last updated", defaultValue = "2023-10-27T10:00:00Z", implementation = String.class, format = "date-time")
    private OffsetDateTime updatedAt;

    public static OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setCustomerName(order.getCustomerName());
        response.setTotalAmount(order.getTotalAmount());
        response.setCurrency(order.getCurrency());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}