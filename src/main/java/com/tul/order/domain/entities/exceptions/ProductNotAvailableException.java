package com.tul.order.domain.entities.exceptions;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProductNotAvailableException extends RuntimeException {

  transient List<UUID> notAvailableProducts;

  public ProductNotAvailableException(String message, List<UUID> notAvailableProducts) {
    super(message);
    this.notAvailableProducts = notAvailableProducts;
  }

}
