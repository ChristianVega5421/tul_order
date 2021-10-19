package com.tul.order.infraestructure.entrypoint.controller;

import static lombok.AccessLevel.PRIVATE;

import com.tul.order.domain.usecases.OrderUseCase;
import com.tul.order.domain.usecases.ProductUseCase;
import com.tul.order.infraestructure.entrypoint.controller.entities.CartControllerRequest;
import com.tul.order.infraestructure.entrypoint.controller.entities.CartControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.entities.CheckoutControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.entities.ProductControllerRequest;
import com.tul.order.infraestructure.entrypoint.controller.entities.ProductControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.mappers.ControllerMapper;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${api.path}/orders")
@FieldDefaults(level = PRIVATE)
@Log4j2
public class OrderController {

  final ProductUseCase productUseCase;
  final OrderUseCase orderUseCase;
  final ControllerMapper controllerMapper;

  @Autowired
  public OrderController(ProductUseCase productUseCase, OrderUseCase orderUseCase,
      ControllerMapper controllerMapper) {
    this.productUseCase = productUseCase;
    this.orderUseCase = orderUseCase;
    this.controllerMapper = controllerMapper;
  }

  @GetMapping("carts/{orderId}")
  public Mono<List<ProductControllerResponse>> getCartProducts(@PathVariable UUID orderId) {
    return productUseCase.getOrderProducts(orderId);
  }

  @PostMapping("carts")
  public Mono<CartControllerResponse> createCart(@RequestBody @Valid CartControllerRequest cart) {
    return orderUseCase.saveCart(controllerMapper.toCart(cart));
  }

  @DeleteMapping("carts/{orderId}/products/{productId}")
  public Mono<Void> deleteProductByCartId(@PathVariable UUID orderId,
      @PathVariable UUID productId) {
    return orderUseCase.deleteProductByCartId(orderId, productId);
  }

  @PutMapping("carts/{orderId}")
  public Mono<UUID> processProductByCartId(@PathVariable UUID orderId,
      @RequestBody @Valid ProductControllerRequest product) {
    return orderUseCase.processProductByCartId(orderId, controllerMapper.toProductByOrder(product));
  }

  @GetMapping("carts/{orderId}/checkout")
  public Mono<CheckoutControllerResponse> checkoutCart(@PathVariable UUID orderId) {
    return orderUseCase.checkoutOrder(orderId);
  }
}
