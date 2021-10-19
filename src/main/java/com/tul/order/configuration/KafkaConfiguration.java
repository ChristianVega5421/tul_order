package com.tul.order.configuration;

import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import com.tul.order.infraestructure.dataprovider.core.KafkaReceiver;
import com.tul.order.infraestructure.dataprovider.core.entities.KafkaProduct;
import com.tul.order.infraestructure.dataprovider.jpa.ProductProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaConfiguration {

  @Value("${kafka.bootstrap.server}")
  String bootstrapAddress;

  @Bean
  public Map<String, Object> kafkaProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(GROUP_ID_CONFIG, "json");
    return props;
  }

  @Bean
  public ConsumerFactory<String, KafkaProduct> processProductFactory() {
    return new DefaultKafkaConsumerFactory<>(kafkaProps(), new StringDeserializer(),
        new JsonDeserializer<>(KafkaProduct.class, false));
  }

  @Bean
  public ConsumerFactory<String, UUID> deleteProductFactory() {
    return new DefaultKafkaConsumerFactory<>(kafkaProps(), new StringDeserializer(),
        new JsonDeserializer<>(UUID.class, false));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, KafkaProduct> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, KafkaProduct> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(processProductFactory());
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, UUID> kafkaDeleteListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, UUID> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(deleteProductFactory());
    return factory;
  }

  @Bean
  public KafkaReceiver kafkaReceiver(ProductProvider productProvider) {
    return new KafkaReceiver(productProvider);
  }

  @Bean
  public Map<String, Object> kafkaSenderProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(CLIENT_ID_CONFIG, "json");
    return props;
  }

  @Bean
  public KafkaSender<String, String> kafkaSender() {
    SenderOptions<String, String> senderOptions = SenderOptions
        .create(kafkaSenderProps());
    return KafkaSender.create(senderOptions);
  }

//  private ObjectMapper objectMapper() {
//    return new ObjectMapper();
//  }

//  @Bean
//  public ProducerFactory<String, List<KafkaCartProduct>> producerKafkaSender() {
//    DefaultKafkaProducerFactory<String, List<KafkaCartProduct>> producerFactory =
//        new DefaultKafkaProducerFactory<>(kafkaSenderProps());
//    producerFactory.setValueSerializer(new JsonSerializer<>(objectMapper()));
//    return producerFactory;
//  }

}
