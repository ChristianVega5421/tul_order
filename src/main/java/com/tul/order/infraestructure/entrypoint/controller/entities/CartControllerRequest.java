package com.tul.order.infraestructure.entrypoint.controller.entities;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
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
public class CartControllerRequest {

  @NotNull
  UUID userId;
  @NotEmpty
  List<@Valid ProductControllerRequest> products;
}
