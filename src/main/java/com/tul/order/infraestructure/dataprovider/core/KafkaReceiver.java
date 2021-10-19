package com.tul.order.infraestructure.dataprovider.core;

import static lombok.AccessLevel.PRIVATE;

import com.tul.order.infraestructure.dataprovider.core.entities.KafkaProduct;
import com.tul.order.infraestructure.dataprovider.jpa.ProductProvider;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.kafka.annotation.KafkaListener;

@FieldDefaults(level = PRIVATE)
@Log4j2
public class KafkaReceiver {

  final CountDownLatch latch = new CountDownLatch(1);
  final ProductProvider productProvider;

  public KafkaReceiver(ProductProvider productProvider) {
    this.productProvider = productProvider;
  }

  private CountDownLatch getLatch() {
    return latch;
  }

  @KafkaListener(topics = "${kafka.process.product.topic}", containerFactory = "kafkaListenerContainerFactory")
  public void receiver(KafkaProduct kafkaProduct) {
    productProvider.saveProductIntoInventory(kafkaProduct)
        .doOnNext(savedUUID -> {
          log.info(new ObjectMessage(savedUUID));
          latch.countDown();
        }).subscribe();
  }

  @KafkaListener(topics = "${kafka.delete.product.topic}", containerFactory = "kafkaDeleteListenerContainerFactory")
  public void receiver(UUID sku) {
    productProvider.deleteInventoryProduct(sku)
        .doOnNext(unused -> latch.countDown())
        .subscribe();
  }
}
