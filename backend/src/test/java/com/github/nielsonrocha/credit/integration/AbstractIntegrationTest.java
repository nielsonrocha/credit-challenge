package com.github.nielsonrocha.credit.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {

  @LocalServerPort protected int port;

  protected RequestSpecification requestSpecification;

  @SuppressWarnings("resource")
  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
          .withDatabaseName("creditos_test")
          .withUsername("postgres")
          .withPassword("postgres");

  @Container
  static KafkaContainer kafka = new KafkaContainer(
      DockerImageName.parse("apache/kafka:3.7.0"));


  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("spring.jpa.show-sql", () -> "true");

    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("spring.kafka.producer.key-serializer",
        () -> "org.apache.kafka.common.serialization.StringSerializer");
    registry.add("spring.kafka.producer.value-serializer",
        () -> "org.springframework.kafka.support.serializer.JsonSerializer");
    registry.add("spring.kafka.consumer.key-deserializer",
        () -> "org.apache.kafka.common.serialization.StringDeserializer");
    registry.add("spring.kafka.consumer.value-deserializer",
        () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
    registry.add("spring.kafka.consumer.group-id", () -> "test-group");
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages", () -> "*");

    registry.add("spring.flyway.enabled", () -> "false");
  }

  @BeforeEach
  void setUp() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    requestSpecification =
        new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(port)
            .setBasePath("/api")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build();
  }
}
