package com.tul.order.infraestructure.entrypoint.controller.entities;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
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
public class CheckoutControllerResponse {
  BigDecimal total;
}
