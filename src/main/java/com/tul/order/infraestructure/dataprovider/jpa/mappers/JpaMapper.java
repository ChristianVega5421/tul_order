package com.tul.order.infraestructure.dataprovider.jpa.mappers;

import static com.tul.order.infraestructure.dataprovider.jpa.entities.StatusEnum.PENDIENTE;

import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.infraestructure.dataprovider.core.entities.KafkaProduct;
import com.tul.order.infraestructure.dataprovider.jpa.entities.OrderJpaEntity;
import com.tul.order.infraestructure.dataprovider.jpa.entities.ProductByOrderJpaEntity;
import com.tul.order.infraestructure.dataprovider.jpa.entities.ProductJpaEntity;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class JpaMapper {

  public ProductJpaEntity toProductJpaEntity(KafkaProduct product, UUID id) {
    return ProductJpaEntity.builder()
        .id(id)
        .stock(product.getStock())
        .sku(product.getSku())
        .build();
  }

  public abstract ProductByOrder toProductByOrder(ProductByOrderJpaEntity productByOrderJpa);

  public OrderJpaEntity toOrderJpaEntity(UUID userId) {
    return OrderJpaEntity.builder()
        .userId(userId)
        .Status(PENDIENTE.name())
        .build();
  }

  public List<ProductByOrderJpaEntity> toProductByOrderJpaEntities(List<ProductByOrder> products,
      UUID orderId) {
    return products.stream().map(product -> toProductByOrderJpaEntity(product, orderId))
        .collect(Collectors.toList());
  }

  protected ProductByOrderJpaEntity toProductByOrderJpaEntity(ProductByOrder product,
      UUID orderId) {
    return ProductByOrderJpaEntity.builder()
        .name(product.getName())
        .orderId(orderId)
        .pricePerUnit(product.getPricePerUnit())
        .quantity(product.getQuantity())
        .sku(product.getSku())
        .build();
  }

  public ProductByOrderJpaEntity toProductJpaEntity(ProductByOrder product, UUID orderId) {
    return ProductByOrderJpaEntity.builder()
        .sku(product.getSku())
        .pricePerUnit(product.getPricePerUnit())
        .name(product.getName())
        .id(product.getId())
        .orderId(orderId)
        .quantity(product.getQuantity())
        .build();
  }

  public ProductByOrderJpaEntity toCombinedProductByOrder(UUID oldProductId, ProductByOrder product,
      UUID orderId) {
    return ProductByOrderJpaEntity.builder()
        .quantity(product.getQuantity())
        .id(oldProductId)
        .name(product.getName())
        .orderId(orderId)
        .pricePerUnit(product.getPricePerUnit())
        .sku(product.getSku())
        .build();
  }

}
