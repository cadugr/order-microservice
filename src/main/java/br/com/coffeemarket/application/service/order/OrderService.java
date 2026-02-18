package br.com.coffeemarket.application.service.order;

import br.com.coffeemarket.adapter.dto.OrderCreateRequest;
import br.com.coffeemarket.adapter.dto.OrderResponse;
import br.com.coffeemarket.application.domain.entity.Order;
import br.com.coffeemarket.application.domain.enuns.OrderStatus;
import br.com.coffeemarket.application.repository.OrderRepository;
import br.com.coffeemarket.application.service.fraud.FraudDetectionService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;
import java.util.UUID;
import jakarta.ws.rs.WebApplicationException;

import static br.com.coffeemarket.adapter.dto.OrderResponse.toResponse;
import static br.com.coffeemarket.application.domain.entity.Order.toEntity;
import static br.com.coffeemarket.application.service.fraud.dto.FraudDetectionRequest.toFraudDetectionRequest;


@ApplicationScoped
public class OrderService {

    private static final String ON_THREAD = " on thread: ";
    private final OrderRepository orderRepository;
    private final FraudDetectionService fraudDetectionService;
    private final Logger log;
    
    
    public OrderService(OrderRepository orderRepository, FraudDetectionService fraudDetectionService, Logger log) {
        this.orderRepository = orderRepository;
        this.fraudDetectionService = fraudDetectionService;
        this.log = log;
    }

    @WithTransaction
    public Uni<OrderResponse> createOrder(OrderCreateRequest request) {
        log.info("Converting OrderCreateRequest to Order entity on thread: " + Thread.currentThread().getName());
        Order order = toEntity(request);

        return fraudDetectionService.checkFraud(toFraudDetectionRequest(request))
                .onItem().transformToUni(fraudResponse -> {
                    if (fraudResponse.isFraudulent()) {
                        log.warn("Order detected as fraudulent: " + fraudResponse.getReason() + ON_THREAD + Thread.currentThread().getName());
                        order.setStatus(OrderStatus.FRAUD_DETECTED);
                    } else {
                        log.info("Order passed fraud check. Score: " + fraudResponse.getScore() + ON_THREAD + Thread.currentThread().getName());
                        order.setStatus(OrderStatus.CREATED);
                    }
                    log.info("Persisting order entity with status: " + order.getStatus() + ON_THREAD + Thread.currentThread().getName());
                    return orderRepository.persistAndFlush(order)
                            .onItem().transform(v -> { // Continue with transform for the final OrderResponse
                                log.info("Order entity persisted, converting to OrderResponse on thread: " + Thread.currentThread().getName());
                                return toResponse(order);
                            });
                });
    }

    @WithTransaction
    public Uni<OrderResponse> findOrderById(UUID id) {
        log.info("Attempting to find order with ID: " + id + ON_THREAD + Thread.currentThread().getName());
        return orderRepository.findById(id)
                .onItem().ifNotNull().transform(order -> {
                    log.info("Order with ID " + id + " found, converting to OrderResponse on thread: " + Thread.currentThread().getName());
                    return toResponse(order);
                })
                .onItem().ifNull().failWith(() -> {
                    log.warn("Order with ID " + id + " not found on thread: " + Thread.currentThread().getName());
                    return new WebApplicationException("Order not found", 404);
                });
    }

}
