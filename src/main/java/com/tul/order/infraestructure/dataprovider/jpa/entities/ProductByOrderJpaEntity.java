package com.tul.order.infraestructure.dataprovider.jpa.entities;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Table(value = "product_by_order")
public class ProductByOrderJpaEntity {

  @Id
  UUID id;
  UUID sku;
  @Column(value = "order_id")
  UUID orderId;
  String name;
  int quantity;
  @Column(value = "price")
  BigDecimal pricePerUnit;
}
