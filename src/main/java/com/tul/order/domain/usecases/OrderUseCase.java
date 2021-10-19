package com.tul.order.domain.usecases;

import com.tul.order.domain.entities.Cart;
import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.infraestructure.entrypoint.controller.entities.CartControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.entities.CheckoutControllerResponse;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface OrderUseCase {

  Mono<CartControllerResponse> saveCart(Cart cart);

  Mono<Void> deleteProductByCartId(UUID orderId, UUID productId);

  Mono<UUID> processProductByCartId(UUID orderId, ProductByOrder product);

  Mono<CheckoutControllerResponse> checkoutOrder(UUID orderId);

}
