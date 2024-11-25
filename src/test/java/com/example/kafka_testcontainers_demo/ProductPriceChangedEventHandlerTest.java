package com.example.kafka_testcontainers_demo;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import com.example.kafka_testcontainers_demo.entity.Product;
import com.example.kafka_testcontainers_demo.event.ProductPriceChangedEvent;
import com.example.kafka_testcontainers_demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@TestPropertySource(
        properties = {
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.datasource.url=jdbc:tc:mysql:8.0.32:///db",
        }
)
@Testcontainers
public class ProductPriceChangedEventHandlerTest {
    @Container
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Product product = new Product(null, "P100", "Product One", BigDecimal.TEN);
        productRepository.save(product);
    }

    @Test
    void shouldHandleProductPriceChangedEvent() {
        ProductPriceChangedEvent event = new ProductPriceChangedEvent(
                "P100",
                new BigDecimal("14.50")
        );

        kafkaTemplate.send("product-price-changes", event.productCode(), event);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    Optional<Product> optionalProduct = productRepository.findByCode(
                            "P100"
                    );
                    assertThat(optionalProduct).isPresent();
                    assertThat(optionalProduct.get().getCode()).isEqualTo("P100");
                    assertThat(optionalProduct.get().getPrice())
                            .isEqualTo(new BigDecimal("14.50"));
                });
    }

}
