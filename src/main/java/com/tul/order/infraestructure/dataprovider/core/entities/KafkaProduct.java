package com.tul.order.infraestructure.dataprovider.core.entities;

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
public class KafkaProduct {

  UUID sku;
  String name;
  String description;
  BigDecimal price;
  int stock;
  int reserved;
}
