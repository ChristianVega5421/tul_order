package com.tul.order.infraestructure.dataprovider.jpa.repository;

import com.tul.order.infraestructure.dataprovider.jpa.entities.OrderJpaEntity;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<OrderJpaEntity, UUID> {

}
