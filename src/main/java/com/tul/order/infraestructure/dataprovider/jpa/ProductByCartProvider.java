package com.tul.order.infraestructure.dataprovider.jpa;

import static lombok.AccessLevel.PRIVATE;

import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.domain.usecases.ports.ProductPort;
import com.tul.order.infraestructure.dataprovider.api.ProductApiProvider;
import com.tul.order.infraestructure.dataprovider.jpa.mappers.JpaMapper;
import com.tul.order.infraestructure.dataprovider.jpa.repository.ProductByOrderRepository;
import java.util.UUID;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = PRIVATE)
public class ProductByCartProvider implements ProductPort {


  final ProductApiProvider productApiProvider;
  final ProductByOrderRepository productByOrderRepository;
  final ProductProvider productProvider;
  final JpaMapper jpaMapper;

  public ProductByCartProvider(ProductProvider productProvider, JpaMapper jpaMapper,
      ProductApiProvider productApiProvider, ProductByOrderRepository productByOrderRepository) {
    this.productProvider = productProvider;
    this.jpaMapper = jpaMapper;
    this.productApiProvider = productApiProvider;
    this.productByOrderRepository = productByOrderRepository;
  }

  @Override
  public Mono<Boolean> checkProductAvailability(UUID sku, int quantity) {
    return productProvider.findProductBySku(sku)
        .flatMap(dbProduct -> {
          if (dbProduct.getStock() > 0 && dbProduct.getStock() >= quantity) {
            return Mono.just(true);
          } else {
            return productApiProvider.checkProductAvailabilityInInventory(sku)
                .map(productApi -> productApi.getStock() > 0 && productApi.getStock() >= quantity);
          }
        })
        .switchIfEmpty(productApiProvider.checkProductAvailabilityInInventory(sku)
            .map(productApi -> productApi.getStock() > 0 && productApi.getStock() >= quantity));
  }

  @Override
  public Mono<Void> deleteProductByCartId(UUID sku) {
    return productByOrderRepository.deleteBySku(sku);
  }

  @Override
  public Mono<ProductByOrder> processProductByCart(ProductByOrder product, UUID orderId) {
    return getProductByOrder(product.getSku(), orderId)
        .flatMap(foundProduct -> {
          if (foundProduct.getId() == null) {
            return productByOrderRepository.save(jpaMapper.toProductJpaEntity(product, orderId))
                .map(jpaMapper::toProductByOrder);
          } else {
            return productByOrderRepository.save(jpaMapper.toCombinedProductByOrder(foundProduct
                    .getId(), product, orderId))
                .map(jpaMapper::toProductByOrder);
          }
        });
  }

  @Override
  public Mono<ProductByOrder> getProductByOrder(UUID sku, UUID orderId) {
    return productByOrderRepository.findBySkuAndOrderId(sku, orderId)
        .map(jpaMapper::toProductByOrder)
        .switchIfEmpty(Mono.just(ProductByOrder.builder().build()));
  }
}
