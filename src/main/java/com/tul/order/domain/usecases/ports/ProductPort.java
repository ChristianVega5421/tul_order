package com.tul.order.domain.usecases.ports;

import com.tul.order.domain.entities.ProductByOrder;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ProductPort {

  Mono<Boolean> checkProductAvailability(UUID sku, int quantity);

  Mono<Void> deleteProductByCartId(UUID deleteProductId);

  Mono<ProductByOrder> processProductByCart(ProductByOrder product, UUID orderId);

  Mono<ProductByOrder> getProductByOrder(UUID sku, UUID orderId);

}
