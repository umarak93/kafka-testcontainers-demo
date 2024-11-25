package com.example.kafka_testcontainers_demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class KafkaTestcontainersDemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
