package com.tul.order.infraestructure.dataprovider.api.entities.exceptions;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(String message) {
    super(message);
  }

}
