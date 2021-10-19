package com.tul.order.domain.entities;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
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
public class Cart {

  UUID userId;
  List<@Valid ProductByOrder> products;

}
