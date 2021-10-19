package com.tul.order.infraestructure.dataprovider.api;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.tul.order.infraestructure.dataprovider.api.entities.ProductApiResponse;
import com.tul.order.infraestructure.dataprovider.api.entities.exceptions.ProductNotFoundException;
import java.util.UUID;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = PRIVATE)
public class ProductApiProvider {

  final WebClient webClient;
  @Value("${inventory.url}")
  String inventoryUrl;

  public ProductApiProvider(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<ProductApiResponse> checkProductAvailabilityInInventory(UUID sku) {
    return webClient.get()
        .uri(String.join("/", inventoryUrl, sku.toString()))
        .retrieve()
        .onStatus(httpStatus -> httpStatus == NOT_FOUND, clientResponse ->
            Mono.error(new ProductNotFoundException("product not found")))
        .bodyToMono(ProductApiResponse.class)
        .onErrorReturn(ProductNotFoundException.class, ProductApiResponse.builder().build());
  }
}
