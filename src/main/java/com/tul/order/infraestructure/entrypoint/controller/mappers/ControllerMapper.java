package com.tul.order.infraestructure.entrypoint.controller.mappers;

import com.tul.order.domain.entities.Cart;
import com.tul.order.domain.entities.ProductByOrder;
import com.tul.order.infraestructure.entrypoint.controller.entities.CartControllerRequest;
import com.tul.order.infraestructure.entrypoint.controller.entities.ProductControllerRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ControllerMapper {

  Cart toCart(CartControllerRequest cartRequest);

  ProductByOrder toProductByOrder(ProductControllerRequest product);

}
