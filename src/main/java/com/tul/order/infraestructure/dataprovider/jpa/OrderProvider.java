package com.tul.order.infraestructure.dataprovider.jpa;

import static com.tul.order.infraestructure.dataprovider.jpa.entities.StatusEnum.COMPLETADO;
import static lombok.AccessLevel.PRIVATE;

import com.tul.order.domain.entities.Cart;
import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.domain.usecases.ports.OrderPort;
import com.tul.order.infraestructure.dataprovider.jpa.entities.OrderJpaEntity;
import com.tul.order.infraestructure.dataprovider.jpa.entities.ProductByOrderJpaEntity;
import com.tul.order.infraestructure.dataprovider.jpa.entities.SavedCart;
import com.tul.order.infraestructure.dataprovider.jpa.mappers.JpaMapper;
import com.tul.order.infraestructure.dataprovider.jpa.repository.OrderRepository;
import com.tul.order.infraestructure.dataprovider.jpa.repository.ProductByOrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = PRIVATE)
public class OrderProvider implements OrderPort {

  final ProductByOrderRepository productByOrderRepository;
  final OrderRepository orderRepository;
  final JpaMapper jpaMapper;

  public OrderProvider(ProductByOrderRepository productByOrderRepository, JpaMapper jpaMapper,
      OrderRepository orderRepository) {
    this.productByOrderRepository = productByOrderRepository;
    this.jpaMapper = jpaMapper;
    this.orderRepository = orderRepository;
  }

  @Override
  public Mono<List<ProductByOrder>> getProductsOrderByOrderId(UUID orderId) {
    return productByOrderRepository.findAllByOrderId(orderId)
        .map(jpaMapper::toProductByOrder)
        .collectList()
        .defaultIfEmpty(new ArrayList<>());
  }

  @Override
  @Transactional
  public Mono<SavedCart> saveCart(Cart cart) {
    return orderRepository.save(jpaMapper.toOrderJpaEntity(cart.getUserId()))
        .flatMap(savedOrder -> productByOrderRepository.saveAll(jpaMapper
                .toProductByOrderJpaEntities(cart.getProducts(), savedOrder.getId())).collectList()
            .map(savedProducts -> savedProducts.stream()
                .map(ProductByOrderJpaEntity::getId).collect(Collectors.toList()))
            .map(productIds -> SavedCart.builder()
                .cartId(savedOrder.getId())
                .productIds(productIds)
                .build()));
  }

  @Override
  public Mono<Boolean> checkOrderExistence(UUID orderId) {
    return orderRepository.existsById(orderId);
  }

  @Override
  public Mono<UUID> checkoutOrder(UUID orderId) {
    return orderRepository.findById(orderId)
        .flatMap(foundOrder -> {
          foundOrder.setStatus(COMPLETADO.name());
          return orderRepository.save(foundOrder)
              .map(OrderJpaEntity::getId);
        });
  }
}
