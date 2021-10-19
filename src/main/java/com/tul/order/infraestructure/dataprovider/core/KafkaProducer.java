package com.tul.order.infraestructure.dataprovider.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.order.infraestructure.dataprovider.core.entities.KafkaCart;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Log4j2
@Service
public class KafkaProducer {

  @Value("${kafka.cart.product.topic}")
  String kafkaCartTopic;
  @Value("${kafka.bootstrap.server}")
  String bootstrapService;
  @Autowired
  KafkaSender<String, String> sender;

  @SneakyThrows
  public void reportProducts(KafkaCart products) {
    String message = new ObjectMapper().writeValueAsString(products);
    CountDownLatch latch = new CountDownLatch(1);
    sender.send(Flux.range(1, 1)
            .map(i -> SenderRecord.create(new ProducerRecord<>(kafkaCartTopic, message),
                UUID.randomUUID())))
        .subscribe(r -> latch.countDown());
  }
}

