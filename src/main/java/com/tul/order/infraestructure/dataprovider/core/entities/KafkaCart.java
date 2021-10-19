package com.tul.order.infraestructure.dataprovider.core.entities;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
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
public class KafkaCart {

  boolean existingOrder;
  List<KafkaCartProduct> cartProducts;
}
