package com.tul.order.common;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
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
public class ErrorResponse {

  List<UUID> notAvailableProducts;
  String message;
}
