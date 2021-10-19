package com.tul.order.infraestructure.entrypoint.controller.entities;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.util.UUID;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class ProductControllerRequest {

  UUID id;
  @NotNull
  UUID sku;
  @NotEmpty
  String name;
  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 10, fraction = 2)
  BigDecimal quantity;
  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 10, fraction = 2)
  BigDecimal pricePerUnit;
}
