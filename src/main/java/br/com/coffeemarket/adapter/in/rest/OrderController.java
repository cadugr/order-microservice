package br.com.coffeemarket.adapter.in.rest;

import br.com.coffeemarket.adapter.in.rest.dto.OrderCreateRequest;
import br.com.coffeemarket.adapter.in.rest.dto.OrderResponse;
import br.com.coffeemarket.application.service.order.OrderService;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Order Resource", description = "Operations related to customer orders")
public class OrderController {

    private static final String ON_THREAD = " on thread: ";

    private final Logger log;

    private final OrderService orderService;

    public OrderController(OrderService orderService, Logger log) {
        this.orderService = orderService;
        this.log = log;
    }

    @POST
    @Operation(summary = "Create a new order",
               description = "Creates a new customer order with the provided details.")
    @APIResponse(responseCode = "201", description = "Order created successfully",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                     schema = @Schema(implementation = OrderResponse.class)))
    @APIResponse(responseCode = "400", description = "Invalid order details provided")
    @RequestBody(description = "Order details to create", required = true,
                 content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                     schema = @Schema(implementation = OrderCreateRequest.class)))
    public Uni<RestResponse<OrderResponse>> createOrder(OrderCreateRequest request) {
        log.info("Received request to create order on thread: " + Thread.currentThread().getName() + " - " + request);
        return orderService.createOrder(request)
                .onItem().transformToUni(orderResponse -> {
                    log.info("Order created successfully with ID: " + orderResponse.getId() + ON_THREAD + Thread.currentThread().getName());
                    return orderService.sendOrderToKafka(orderResponse) // Send to Kafka after order creation
                            .replaceWith(RestResponse.ResponseBuilder
                                    .<OrderResponse>created(URI.create("/orders/" + orderResponse.getId()))
                                    .entity(orderResponse)
                                    .build());
                });
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Retrieve an order by ID",
               description = "Retrieves a single customer order based on its unique identifier.")
    @APIResponse(responseCode = "200", description = "Order found",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                     schema = @Schema(implementation = OrderResponse.class)))
    @APIResponse(responseCode = "404", description = "Order not found")
    public Uni<OrderResponse> getOrderById(
            @Parameter(description = "Unique identifier of the order", required = true)
            @PathParam("id") UUID id) {
        log.info("Received request to retrieve order with ID: " + id + ON_THREAD + Thread.currentThread().getName());
        return orderService.findOrderById(id)
                .onItem().invoke(orderResponse -> log.info("Order with ID " + id + " retrieved successfully on thread: " + Thread.currentThread().getName()))
                .onFailure().invoke(throwable -> log.error("Failed to retrieve order with ID " + id + ": " + throwable.getMessage() + ON_THREAD + Thread.currentThread().getName()));
    }
}
