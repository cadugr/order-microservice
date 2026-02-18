package br.com.coffeemarket.adapter;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import io.quarkus.test.InjectMock;
import org.mockito.Mockito;
import br.com.coffeemarket.application.service.kafka.OrderKafkaProducer;
import io.smallrye.mutiny.Uni;

@QuarkusTest
class OrderControllerTest {

    @InjectMock
    OrderKafkaProducer orderKafkaProducer;

    @Test
    void testCreateOrderEndpoint() {
        String requestBody = "{ \"orderDate\": \"2023-10-27T10:00:00Z\", \"customerName\": \"Test Customer\", \"totalAmount\": 150.75, \"currency\": \"USD\", \"status\": \"PENDING\" }";

        // Mock Kafka producer behavior
        Mockito.when(orderKafkaProducer.sendOrderToKafka(Mockito.any()))
                .thenReturn(Uni.createFrom().voidItem());

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/orders")
            .then()
            .statusCode(201)
            .body("id", notNullValue());

        // Verify that the Kafka producer was called
        Mockito.verify(orderKafkaProducer, Mockito.times(1))
                .sendOrderToKafka(Mockito.any());
    }

    @Test
    void testGetOrderByIdEndpointNotFound() {
        given()
            .when()
            .get("/orders/12345678-1234-1234-1234-1234567890ab") // Non-existent UUID
            .then()
            .statusCode(404);
    }
}
