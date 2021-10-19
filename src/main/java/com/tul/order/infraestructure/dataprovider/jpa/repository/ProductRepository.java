package com.tul.order.infraestructure.dataprovider.jpa.repository;

import com.tul.order.infraestructure.dataprovider.jpa.entities.ProductJpaEntity;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductJpaEntity, UUID> {

  Mono<ProductJpaEntity> findBySku(UUID sku);


}
