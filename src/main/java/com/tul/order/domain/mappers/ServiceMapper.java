package com.tul.order.domain.mappers;

import static com.tul.order.domain.entities.ProductStatusEnum.RESERVED;

import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.domain.entities.ProductStatusEnum;
import com.tul.order.infraestructure.dataprovider.core.entities.KafkaCart;
import com.tul.order.infraestructure.dataprovider.core.entities.KafkaCartProduct;
import com.tul.order.infraestructure.dataprovider.jpa.entities.SavedCart;
import com.tul.order.infraestructure.entrypoint.controller.entities.CartControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.entities.CheckoutControllerResponse;
import com.tul.order.infraestructure.entrypoint.controller.entities.ProductControllerResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class ServiceMapper {

  protected abstract List<ProductControllerResponse> toProducts(List<ProductByOrder> products);

  public List<ProductControllerResponse> toProductsOrders(List<ProductByOrder> dbProducts) {
    List<ProductControllerResponse> products = toProducts(dbProducts);
    return products.stream().peek(product -> product.setTotalPrice(product.getPricePerUnit()
        .multiply(BigDecimal.valueOf(product.getQuantity())))).collect(Collectors.toList());
  }

  public abstract CartControllerResponse toCartControllerResponse(SavedCart savedCart);

  public KafkaCart toKafkaCart(List<ProductByOrder> cartProducts,
      boolean existingOrder) {
    return KafkaCart.builder()
        .existingOrder(existingOrder)
        .cartProducts(toKafkaCartProducts(cartProducts))
        .build();
  }

  protected List<KafkaCartProduct> toKafkaCartProducts(List<ProductByOrder> cartProducts) {
    return cartProducts.stream().map(product -> KafkaCartProduct.builder()
            .quantity(product.getQuantity())
            .sku(product.getSku())
            .status(RESERVED.name())
            .build())
        .collect(Collectors.toList());
  }


  @Mapping(target = "status", expression = "java(getProductStatus(\"RESERVED\"))")
  public abstract KafkaCartProduct toKafkaCartProduct(ProductByOrder product, int oldQuantity);

  @Named("deleteProduct")
  @Mapping(target = "status", expression = "java(getProductStatus(\"STOCK\"))")
  public abstract KafkaCartProduct KafkaDeleteCartProduct(ProductByOrder product);

  @Named("soldProduct")
  @Mapping(target = "status", expression = "java(getProductStatus(\"SOLD\"))")
  public abstract KafkaCartProduct KafkaSoldCartProduct(ProductByOrder product);

  public abstract CheckoutControllerResponse toCheckoutControllerResponse(BigDecimal total);

  @Named("enumMapper")
  protected String getProductStatus(String name) {
    return ProductStatusEnum.valueOf(name).name();
  }

  public List<KafkaCartProduct> toSoldKafkaCartProducts(List<ProductByOrder> soldProducts) {
    return soldProducts.stream().map(this::KafkaSoldCartProduct)
        .collect(Collectors.toList());
  }
}
