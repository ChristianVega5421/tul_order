package com.tul.order.infraestructure.dataprovider.jpa.repository;

import com.tul.order.infraestructure.dataprovider.jpa.entities.ProductByOrderJpaEntity;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductByOrderRepository extends
    ReactiveCrudRepository<ProductByOrderJpaEntity, UUID> {

  Flux<ProductByOrderJpaEntity> findAllByOrderId(UUID orderId);

  Mono<ProductByOrderJpaEntity> findBySkuAndOrderId(UUID sku, UUID orderId);

  Mono<Void> deleteBySku(UUID sku);

}
