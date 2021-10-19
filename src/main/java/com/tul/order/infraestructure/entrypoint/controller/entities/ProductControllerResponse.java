package com.tul.order.infraestructure.entrypoint.controller.entities;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class ProductControllerResponse {

  UUID id;
  UUID sku;
  String name;
  int quantity;
  BigDecimal pricePerUnit;
  BigDecimal totalPrice;

}
