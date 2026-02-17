package br.com.coffeemarket.application.domain.enuns;

public enum OrderStatus {
    CREATED,
    PENDING, // Assuming PENDING is also a valid status from the DTO
    FRAUD_DETECTED,
    // Add other statuses as needed (e.g., SHIPPED, DELIVERED, CANCELLED)
}

