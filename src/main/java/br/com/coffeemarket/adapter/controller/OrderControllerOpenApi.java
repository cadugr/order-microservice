package br.com.coffeemarket.adapter.controller;

import br.com.coffeemarket.adapter.dto.OrderCreateRequest;
import br.com.coffeemarket.adapter.dto.OrderResponse;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.UUID;

public interface OrderControllerOpenApi {

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
    Uni<RestResponse<OrderResponse>> createOrder(OrderCreateRequest request);

    @GET
    @Path("/{id}")
    @Operation(summary = "Retrieve an order by ID",
            description = "Retrieves a single customer order based on its unique identifier.")
    @APIResponse(responseCode = "200", description = "Order found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderResponse.class)))
    @APIResponse(responseCode = "404", description = "Order not found")
    Uni<OrderResponse> getOrderById(
            @Parameter(description = "Unique identifier of the order", required = true)
            @PathParam("id") UUID id);
}
