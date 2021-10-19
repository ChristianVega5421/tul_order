package com.tul.order.domain.usecases;

import com.tul.order.infraestructure.entrypoint.controller.entities.ProductControllerResponse;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ProductUseCase {

  Mono<List<ProductControllerResponse>> getOrderProducts(UUID orderId);

}
