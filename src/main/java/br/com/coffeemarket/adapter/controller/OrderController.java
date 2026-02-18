package br.com.coffeemarket.adapter.controller;

import br.com.coffeemarket.adapter.dto.OrderCreateRequest;
import br.com.coffeemarket.adapter.dto.OrderResponse;
import br.com.coffeemarket.application.service.order.OrderService;
import br.com.coffeemarket.application.service.kafka.OrderKafkaProducer; // Added import
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Order Resource", description = "Operations related to customer orders") // Added Tag
public class OrderController implements OrderControllerOpenApi {

    private static final String ON_THREAD = " on thread: ";

    private final Logger log;

    private final OrderService orderService;
    private final OrderKafkaProducer orderKafkaProducer; // Injected OrderKafkaProducer

    public OrderController(OrderService orderService,
                           Logger log,
                           OrderKafkaProducer orderKafkaProducer) {
        this.orderService = orderService;
        this.log = log;
        this.orderKafkaProducer = orderKafkaProducer;
    }

    @POST
    public Uni<RestResponse<OrderResponse>> createOrder(OrderCreateRequest request) {
        log.info("Received request to create order on thread: " + Thread.currentThread().getName() + " - " + request);
        return orderService.createOrder(request)
                .onItem().transformToUni(orderResponse -> {
                    log.info("Order created successfully with ID: " + orderResponse.getId() + ON_THREAD + Thread.currentThread().getName());
                    return orderKafkaProducer.sendOrderToKafka(orderResponse) // Send to Kafka after order creation
                            .replaceWith(RestResponse.ResponseBuilder
                                    .<OrderResponse>created(URI.create("/orders/" + orderResponse.getId()))
                                    .entity(orderResponse)
                                    .build());
                });
    }

    @GET
    @Path("/{id}")
    public Uni<OrderResponse> getOrderById(
            @Parameter(description = "Unique identifier of the order", required = true)
            @PathParam("id") UUID id) {
        log.info("Received request to retrieve order with ID: " + id + ON_THREAD + Thread.currentThread().getName());
        return orderService.findOrderById(id)
                .onItem().invoke(orderResponse -> log.info("Order with ID " + id + " retrieved successfully on thread: " + Thread.currentThread().getName()))
                .onFailure().invoke(throwable -> log.error("Failed to retrieve order with ID " + id + ": " + throwable.getMessage() + ON_THREAD + Thread.currentThread().getName()));
    }
}
