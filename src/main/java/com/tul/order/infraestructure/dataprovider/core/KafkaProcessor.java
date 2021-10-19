package com.tul.order.infraestructure.dataprovider.core;

import static lombok.AccessLevel.PRIVATE;

import com.tul.order.infraestructure.dataprovider.core.entities.KafkaProduct;
import com.tul.order.infraestructure.dataprovider.jpa.ProductProvider;
import java.util.UUID;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = PRIVATE)
public class KafkaProcessor {

  final ProductProvider productProvider;

  public KafkaProcessor(ProductProvider productProvider) {
    this.productProvider = productProvider;
  }

  public Mono<UUID> processInventoryProduct(KafkaProduct kafkaProduct) {
    return productProvider.saveProductIntoInventory(kafkaProduct);
  }

  public Mono<Void> deleteInventoryProduct(UUID sku) {
    return productProvider.deleteInventoryProduct(sku);
  }

}
