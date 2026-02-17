package br.com.coffeemarket.application.service.order;

import br.com.coffeemarket.adapter.in.rest.dto.OrderCreateRequest;
import br.com.coffeemarket.adapter.in.rest.dto.OrderResponse;
import br.com.coffeemarket.application.domain.entity.Order;
import br.com.coffeemarket.application.domain.enuns.OrderStatus;
import br.com.coffeemarket.application.repository.OrderRepository;
import br.com.coffeemarket.application.service.fraud.FraudDetectionService;
import br.com.coffeemarket.application.service.fraud.dto.FraudDetectionRequest;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import org.jboss.logging.Logger;
import java.util.UUID;
import jakarta.ws.rs.WebApplicationException;


@ApplicationScoped
public class OrderService {

    private static final String ON_THREAD = " on thread: ";
    private final OrderRepository orderRepository;
    private final FraudDetectionService fraudDetectionService;
    private final Logger log;
    private final Emitter<OrderResponse> orderEmitter;
    
    public OrderService(OrderRepository orderRepository, FraudDetectionService fraudDetectionService, Logger log,
                        @Channel("orders-out") Emitter<OrderResponse> orderEmitter) {
        this.orderRepository = orderRepository;
        this.fraudDetectionService = fraudDetectionService;
        this.log = log;
        this.orderEmitter = orderEmitter;
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

    public Uni<Void> sendOrderToKafka(OrderResponse orderResponse) {
        log.info("Sending order to Kafka topic 'orders-out' after transaction commit: " + orderResponse.getId() + ON_THREAD + Thread.currentThread().getName());
        return Uni.createFrom().completionStage(orderEmitter.send(orderResponse));
    }

    private Order toEntity(OrderCreateRequest request) {
        Order order = new Order();
        order.setOrderDate(request.getOrderDate());
        order.setCustomerName(request.getCustomerName());
        order.setTotalAmount(request.getTotalAmount());
        order.setCurrency(request.getCurrency());
        order.setStatus(request.getStatus());
        return order;
    }

    private FraudDetectionRequest toFraudDetectionRequest(OrderCreateRequest request) {
        FraudDetectionRequest fraudRequest = new FraudDetectionRequest();
        fraudRequest.setOrderDate(request.getOrderDate());
        fraudRequest.setCustomerName(request.getCustomerName());
        fraudRequest.setTotalAmount(request.getTotalAmount());
        fraudRequest.setCurrency(request.getCurrency());
        return fraudRequest;
    }

    private OrderResponse toResponse(Order order) {
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
