package com.tul.order.infraestructure.entrypoint.controller.exceptionhandlers;

import com.tul.order.common.ErrorResponse;
import com.tul.order.domain.entities.exceptions.OrderNotFoundException;
import com.tul.order.domain.entities.exceptions.ProductNotAvailableException;
import com.tul.order.infraestructure.dataprovider.api.entities.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler({OrderNotFoundException.class, ProductNotFoundException.class})
  protected Mono<ResponseEntity<ErrorResponse>> handleNotFoundException(
      OrderNotFoundException exception) {
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder()
        .message(exception.getMessage())
        .build()));
  }

  @ExceptionHandler(ProductNotAvailableException.class)
  protected Mono<ResponseEntity<ErrorResponse>> handleProductNotAvailableException(
      ProductNotAvailableException exception) {
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder()
        .message(exception.getMessage())
        .notAvailableProducts(exception.getNotAvailableProducts())
        .build()));
  }
}
