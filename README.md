# Order Microservice

This project implements a reactive Order Microservice using Quarkus, the Supersonic Subatomic Java Framework. It provides functionalities for managing customer orders, including creation and retrieval, and integrates a basic AI-driven fraud detection mechanism.

## Key Technologies & Features

*   **Quarkus:** Supersonic Subatomic Java Framework.
*   **Reactive Programming:** End-to-end reactivity using Mutiny and Hibernate Reactive Panache.
*   **PostgreSQL:** Database for order persistence.
*   **Liquibase:** Database schema migration.
*   **REST & Jackson:** Reactive REST endpoints with JSON serialization/deserialization.
*   **SmallRye OpenAPI:** Automatic API documentation (Swagger UI).
*   **Lombok:** Boilerplate code reduction.
*   **AI Fraud Detection:** A simulated AI service for detecting fraudulent orders.

## Prerequisites

Before running this project, ensure you have the following installed:

*   **Java Development Kit (JDK) 21 or newer.**
*   **Apache Maven** (version compatible with your JDK, or use the provided `mvnw` wrapper).
*   **Docker Desktop** (or a compatible Docker environment) for building native images in containers and running services via Docker Compose.

## Running the application in Dev Mode (JVM)

For local development with live coding and Quarkus Dev UI:

1.  **Start the application:**
    ```shell script
    ./mvnw quarkus:dev
    ```
2.  **Access Dev UI:** The Quarkus Dev UI is available at <http://localhost:8080/q/dev/>.
3.  **Access Swagger UI:** The OpenAPI documentation (Swagger UI) is available at <http://localhost:8080/q/swagger-ui>.
    *   _Note:_ In dev mode, Quarkus Dev Services will automatically start a PostgreSQL container for you.

## Running the application with Docker (Native Image)

For a production-ready deployment with minimal footprint and fast startup times, we build a native executable and run it in a micro Docker image.

1.  **Build the native executable:**
    This command compiles your Quarkus application into a native executable using GraalVM, performed inside a Docker container for consistency. This process can take several minutes.
    ```shell script
    ./mvnw clean package -Dnative -Dquarkus.native.container-build=true
    ```
    _Note:_ The `application.yml` is configured to initialize `br.com.coffeemarket.config.utility.IdGenerator` at runtime for native builds.

2.  **Build the Docker image for the native application:**
    This command uses the `src/main/docker/Dockerfile.native-micro` to create a small Docker image containing your native executable.
    ```shell script
    docker build -f src/main/docker/Dockerfile.native-micro -t order-service:1.0.0-native .
    ```

3.  **Start the services using Docker Compose:**
    This command will start both the PostgreSQL database and your native Quarkus application, connected via a custom Docker network.
    ```shell script
    docker-compose -f docker/docker-compose.yml up -d
    ```

4.  **Verify the running containers:**
    ```shell script
    docker ps
    ```
    You should see `orders-db` and `order-service` containers running.

5.  **Stop the services:**
    ```shell script
    docker-compose -f docker/docker-compose.yml down -v
    ```

## Testing the API

Once the application is running (either in dev mode or via Docker Compose), you can test the API endpoints.

### Access Swagger UI

Open your browser and navigate to:
<http://localhost:8080/q/swagger-ui>
Here you can explore the API documentation and interact with the endpoints.

### Create a New Order (POST /orders)

Use `curl` to create a new order. Observe the `status` in the response and the application logs for fraud detection messages.

*   **Normal Order:**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{ "orderDate": "2023-10-27T10:00:00Z", "customerName": "Jane Doe", "totalAmount": 99.99, "currency": "BRL", "status": "PENDING" }' http://localhost:8080/orders
    ```
    _Expected Status:_ `CREATED`

*   **Fraudulent Order (High Amount):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{ "orderDate": "2023-10-27T10:00:00Z", "customerName": "High Roller", "totalAmount": 1200.00, "currency": "USD", "status": "PENDING" }' http://localhost:8080/orders
    ```
    _Expected Status:_ `FRAUD_DETECTED`

*   **Fraudulent Order (Suspicious Name):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{ "orderDate": "2023-10-27T10:00:00Z", "customerName": "Fraudster", "totalAmount": 50.00, "currency": "EUR", "status": "PENDING" }' http://localhost:8080/orders
    ```
    _Expected Status:_ `FRAUD_DETECTED`

### Retrieve an Order by ID (GET /orders/{id})

Replace `<ORDER_ID>` with the `id` obtained from a successful `POST /orders` response.

```bash
curl -X GET http://localhost:8080/orders/<ORDER_ID>
```

## Running Tests

To run the unit and integration tests:

```shell script
./mvnw test
```
_Note:_ Quarkus Dev Services will automatically start a PostgreSQL container for tests. Observe the console output for log messages containing thread names (`vert.x-eventloop-thread-X` for non-blocking operations, `vert.x-worker-thread-X` for offloaded blocking operations) to verify reactivity.