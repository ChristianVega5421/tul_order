package com.tul.order.domain.usecases.ports;

import com.tul.order.domain.entities.Cart;
import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.infraestructure.dataprovider.jpa.entities.SavedCart;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface OrderPort {

  Mono<List<ProductByOrder>> getProductsOrderByOrderId(UUID orderId);

  Mono<SavedCart> saveCart(Cart cart);

  Mono<Boolean> checkOrderExistence(UUID orderId);

  Mono<UUID> checkoutOrder(UUID orderId);


}
