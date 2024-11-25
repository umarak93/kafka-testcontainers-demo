package com.example.kafka_testcontainers_demo.event;
import java.math.BigDecimal;

public record ProductPriceChangedEvent(String productCode, BigDecimal price) {}

