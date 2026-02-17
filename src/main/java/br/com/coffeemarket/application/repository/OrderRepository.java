package br.com.coffeemarket.application.repository;

import br.com.coffeemarket.application.domain.entity.Order;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<Order, UUID> {
    // Panache provides basic CRUD operations reactively.
    // Custom reactive methods can be added here if needed.
}
