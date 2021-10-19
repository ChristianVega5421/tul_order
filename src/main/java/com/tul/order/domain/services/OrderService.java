package com.tul.order.domain.services;

import static lombok.AccessLevel.PRIVATE;
import static reactor.core.scheduler.Schedulers.parallel;

import com.tul.order.domain.entities.Cart;
import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.domain.entities.exceptions.OrderNotFoundException;
import com.tul.order.domain.entities.exceptions.ProductNotAvailableException;
import com.tul.order.domain.mappers.ServiceMapper;
import com.tul.order.domain.usecases.OrderUseCase;
import com.tul.order.domain.usecases.ProductUseCase;
import com.tul.order.domain.usecases.ports.OrderPort;
import com.tul.order.domain.usecases.ports.ProductPort;
import com.tul.order.infraestructure.dataprovider.core.KafkaProducer;
import com.tul.order.infraestructure.dataprovider.core.entities.KafkaCart;
import com.tul.order.infraestructure.entrypoint.controller.entities.CartControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.entities.CheckoutControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.entities.ProductControllerResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = PRIVATE)
public class OrderService implements ProductUseCase, OrderUseCase {

  final ProductPort productPort;
  final OrderPort orderPort;
  final ServiceMapper serviceMapper;
  final KafkaProducer kafkaProducer;

  public OrderService(ProductPort productPort, OrderPort orderPort, ServiceMapper serviceMapper,
      KafkaProducer kafkaProducer) {
    this.productPort = productPort;
    this.serviceMapper = serviceMapper;
    this.orderPort = orderPort;
    this.kafkaProducer = kafkaProducer;
  }

  @Override
  public Mono<List<ProductControllerResponse>> getOrderProducts(UUID orderId) {
    return orderPort.getProductsOrderByOrderId(orderId)
        .flatMap(products -> {
          if (!products.isEmpty()) {
            return Mono.just(serviceMapper.toProductsOrders(products));
          }
          return Mono.error(() ->
              new OrderNotFoundException("Order not found: " + orderId.toString()));
        });
  }

  @Override
  public Mono<CartControllerResponse> saveCart(Cart cart) {
    return Flux.fromIterable(cart.getProducts())
        .publishOn(parallel())
        .flatMap(product -> productPort.checkProductAvailability(product.getSku(), product
                .getQuantity())
            .map(isAvailable -> Map.of(product.getSku(), isAvailable)))
        .reduce(new HashMap<UUID, Boolean>(), (productCollection, newProduct) -> {
          productCollection.putAll(newProduct);
          return productCollection;
        })
        .flatMap(productAvailableList -> {
          if (productAvailableList.entrySet().stream().anyMatch(entrySet -> !entrySet.getValue())) {
            return Mono.error(() ->
                new ProductNotAvailableException("Some products are not available",
                    productAvailableList.entrySet().stream()
                        .filter(entrySet -> !entrySet.getValue()).map(Entry::getKey)
                        .collect(Collectors.toList())));
          }
          return orderPort.saveCart(cart)
              .map(savedCart -> {
                kafkaProducer.reportProducts(serviceMapper.toKafkaCart(cart
                    .getProducts(), false));
                return serviceMapper.toCartControllerResponse(savedCart);
              });
        });
  }

  @Override
  public Mono<Void> deleteProductByCartId(UUID orderId, UUID sku) {
    return orderPort.checkOrderExistence(orderId)
        .flatMap(orderExists -> {
          if (orderExists) {
            return productPort.getProductByOrder(sku, orderId)
                .flatMap(foundProduct -> {
                  if (foundProduct.getSku() != null) {
                    kafkaProducer.reportProducts(KafkaCart.builder().cartProducts(List
                            .of(serviceMapper.KafkaDeleteCartProduct(foundProduct)))
                        .existingOrder(true)
                        .build());
                    return productPort.deleteProductByCartId(sku);
                  }
                  return Mono.empty();
                });
          }
          return Mono.error(() ->
              new OrderNotFoundException("Order not found: " + orderId.toString()));
        });
  }

  @Override
  public Mono<UUID> processProductByCartId(UUID orderId, ProductByOrder product) {
    return orderPort.checkOrderExistence(orderId)
        .flatMap(orderExists -> {
          if (orderExists) {
            return productPort.checkProductAvailability(product.getSku(), product.getQuantity())
                .flatMap(isAvailable -> {
                  if (isAvailable) {
                    return productPort.getProductByOrder(product.getSku(), orderId)
                        .flatMap(oldProduct -> productPort.processProductByCart(product, orderId)
                            .map(savedProduct -> {
                              kafkaProducer.reportProducts(KafkaCart.builder()
                                  .cartProducts(List.of(serviceMapper
                                      .toKafkaCartProduct(savedProduct, oldProduct.getQuantity())))
                                  .existingOrder(true)
                                  .build());
                              return savedProduct.getId();
                            }));
                  }
                  return Mono.error(
                      () -> new ProductNotAvailableException("Some products are not available",
                          List.of(product.getId())));
                });
          }
          return Mono.error(() ->
              new OrderNotFoundException("Order not found: " + orderId.toString()));
        });
  }

  @Override
  public Mono<CheckoutControllerResponse> checkoutOrder(UUID orderId) {
    return orderPort.getProductsOrderByOrderId(orderId)
        .flatMap(order -> Flux.fromIterable(order)
            .subscribeOn(parallel())
            .map(product -> product.getPricePerUnit().multiply(BigDecimal.valueOf(product
                .getQuantity())))
            .reduce(new BigDecimal(0), BigDecimal::add)
            .flatMap(total ->
                orderPort.checkoutOrder(orderId)
                    .map(savedOrderId -> {
                      kafkaProducer.reportProducts(KafkaCart.builder()
                          .cartProducts(serviceMapper.toSoldKafkaCartProducts(order))
                          .existingOrder(true)
                          .build());
                      return serviceMapper.toCheckoutControllerResponse(total);
                    })));
  }
}
