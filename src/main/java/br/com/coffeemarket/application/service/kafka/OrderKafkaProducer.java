package br.com.coffeemarket.application.service.kafka;

import br.com.coffeemarket.adapter.dto.OrderResponse;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderKafkaProducer {

    private static final String ON_THREAD = " on thread: ";
    private final Logger log;
    private final Emitter<OrderResponse> orderEmitter;

    public OrderKafkaProducer(Logger log,
                              @Channel("orders-out") Emitter<OrderResponse> orderEmitter) {
        this.log = log;
        this.orderEmitter = orderEmitter;
    }

    public Uni<Void> sendOrderToKafka(OrderResponse orderResponse) {
        log.info("Sending order to Kafka topic 'orders-out': " + orderResponse.getId() + ON_THREAD + Thread.currentThread().getName());
        return Uni.createFrom().completionStage(orderEmitter.send(orderResponse));
    }
}
