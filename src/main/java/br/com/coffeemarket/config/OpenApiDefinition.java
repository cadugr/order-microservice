package br.com.coffeemarket.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Order Microservice",
        version = "1.0.0",
        description = "This microservice provides functionalities for managing customer orders, including creation, retrieval, and status updates. It is built with Quarkus and leverages reactive programming for high performance.",
        contact = @Contact(
            name = "Coffee Market Support",
            url = "https://www.coffeemarket.com/support",
            email = "support@coffeemarket.com"
        )
    ),
    tags = {
        @Tag(name = "Order Resource", description = "Operations related to customer orders")
    }
)
public class OpenApiDefinition extends Application {
}
