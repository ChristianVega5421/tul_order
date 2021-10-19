package com.tul.order.infraestructure.dataprovider.jpa;

import static lombok.AccessLevel.PRIVATE;

import com.tul.order.infraestructure.dataprovider.core.entities.KafkaProduct;
import com.tul.order.infraestructure.dataprovider.jpa.entities.ProductJpaEntity;
import com.tul.order.infraestructure.dataprovider.jpa.mappers.JpaMapper;
import com.tul.order.infraestructure.dataprovider.jpa.repository.ProductRepository;
import java.util.UUID;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = PRIVATE)
public class ProductProvider {

  final ProductRepository productRepository;
  final JpaMapper jpaMapper;

  public ProductProvider(ProductRepository productRepository, JpaMapper jpaMapper) {
    this.productRepository = productRepository;
    this.jpaMapper = jpaMapper;
  }

  public Mono<ProductJpaEntity> findProductBySku(UUID sku) {
    return productRepository.findBySku(sku);
  }

  public Mono<UUID> saveProductIntoInventory(KafkaProduct kafkaProduct) {
    return productRepository.findBySku(kafkaProduct.getSku())
        .flatMap(existingProduct -> productRepository.save(jpaMapper
                .toProductJpaEntity(kafkaProduct, existingProduct.getId()))
            .map(ProductJpaEntity::getId))
        .switchIfEmpty(productRepository.save(jpaMapper.toProductJpaEntity(kafkaProduct, null))
            .map(ProductJpaEntity::getId));
  }

  public Mono<Void> deleteInventoryProduct(UUID sku) {
    return productRepository.findBySku(sku)
        .flatMap(productRepository::delete);
  }

}
