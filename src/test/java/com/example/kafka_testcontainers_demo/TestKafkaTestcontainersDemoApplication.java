package com.example.kafka_testcontainers_demo;

import org.springframework.boot.SpringApplication;

public class TestKafkaTestcontainersDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(KafkaTestcontainersDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
