package br.com.coffeemarket.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class OrderControllerTest {

    @Test
    void testCreateOrderEndpoint() {
        String requestBody = "{ \"orderDate\": \"2023-10-27T10:00:00Z\", \"customerName\": \"Test Customer\", \"totalAmount\": 150.75, \"currency\": \"USD\", \"status\": \"PENDING\" }";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/orders")
            .then()
            .statusCode(201)
            .body("id", notNullValue());
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
