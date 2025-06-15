package com.github.nielsonrocha.credit.integration;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.github.nielsonrocha.credit.dto.EventoConsultaDTO;
import com.github.nielsonrocha.credit.entity.Credito;
import com.github.nielsonrocha.credit.repository.CreditoRepository;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("Testes de Integração - CreditoController")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CreditoControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired private CreditoRepository creditoRepository;

  private Consumer<String, Object> kafkaConsumer;

  @Value("${app.credito-topic:credito-consulta-topic}")
  private String topico;

  @BeforeEach
  void setUpTest() {
    // Configurar consumer Kafka para testes
    Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-group", "true");
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

    kafkaConsumer = new DefaultKafkaConsumerFactory<String, Object>(consumerProps).createConsumer();
    kafkaConsumer.subscribe(Collections.singletonList("credito-consulta-topic"));
  }

  @AfterEach
  void tearDownTest() {
    if (kafkaConsumer != null) {
      kafkaConsumer.close();
    }
    creditoRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve retornar lista de créditos por NFS-e")
  void deveRetornarCreditosPorNfse() {
    // Given
    String numeroNfse = "7891011";
    criarCreditosParaTeste(numeroNfse);

    // When & Then
    Response response =
        given()
            .spec(requestSpecification)
            .pathParam("numeroNfse", numeroNfse)
            .when()
            .get("/creditos/{numeroNfse}")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("$", hasSize(2))
            .body("[0].numeroNfse", equalTo(numeroNfse))
            .body("[0].numeroCredito", notNullValue())
            .body("[0].valorIssqn", notNullValue())
            .body("[0].tipoCredito", equalTo("ISSQN"))
            .body("[0].simplesNacional", anyOf(equalTo("Sim"), equalTo("Não")))
            .body("[1].numeroNfse", equalTo(numeroNfse))
            .extract()
            .response();

    // Verificar evento Kafka
    verificarEventoKafka("NFSE", numeroNfse);

    // Log da resposta para debug
    System.out.println("Response: " + response.asString());
  }

  @Test
  @DisplayName("Deve retornar crédito específico por número")
  void deveRetornarCreditoPorNumero() {
    // Given
    Credito credito = criarCreditoParaTeste();
    String numeroCredito = credito.getNumeroCredito();

    // When & Then
    given()
        .spec(requestSpecification)
        .pathParam("numeroCredito", numeroCredito)
        .when()
        .get("/creditos/credito/{numeroCredito}")
        .then()
        .statusCode(200)
        .contentType("application/json")
        .body("numeroCredito", equalTo(numeroCredito))
        .body("numeroNfse", equalTo(credito.getNumeroNfse()))
        .body("valorIssqn", equalTo(credito.getValorIssqn().floatValue()))
        .body("tipoCredito", equalTo(credito.getTipoCredito()))
        .body("aliquota", equalTo(credito.getAliquota().floatValue()))
        .body("valorFaturado", equalTo(credito.getValorFaturado().floatValue()))
        .body("valorDeducao", equalTo(credito.getValorDeducao().floatValue()))
        .body("baseCalculo", equalTo(credito.getBaseCalculo().floatValue()));

    // Verificar evento Kafka
    verificarEventoKafka("CREDITO", numeroCredito);
  }

  @Test
  @DisplayName("Deve retornar 404 quando NFS-e não encontrada")
  void deveRetornar404QuandoNfseNaoEncontrada() {
    // Given
    String numeroNfseInexistente = "999999999";

    // When & Then
    given()
        .spec(requestSpecification)
        .pathParam("numeroNfse", numeroNfseInexistente)
        .when()
        .get("/creditos/{numeroNfse}")
        .then()
        .statusCode(404)
        .contentType("application/json")
        .body("status", equalTo(404))
        .body("error", equalTo("Not Found"))
        .body("message", containsString("Nenhum crédito encontrado"))
        .body("timestamp", notNullValue())
        .body("path", notNullValue());
  }

  @Test
  @DisplayName("Deve retornar 404 quando número do crédito não encontrado")
  void deveRetornar404QuandoCreditoNaoEncontrado() {
    // Given
    String numeroCreditoInexistente = "999999";

    // When & Then
    given()
        .spec(requestSpecification)
        .pathParam("numeroCredito", numeroCreditoInexistente)
        .when()
        .get("/creditos/credito/{numeroCredito}")
        .then()
        .statusCode(404)
        .contentType("application/json")
        .body("status", equalTo(404))
        .body("error", equalTo("Not Found"))
        .body("message", containsString("Crédito não encontrado"))
        .body("timestamp", notNullValue());
  }

  @Test
  @DisplayName("Deve validar schema JSON da resposta")
  void deveValidarSchemaJsonDaResposta() {
    // Given
    String numeroNfse = "7891011";
    criarCreditosParaTeste(numeroNfse);

    // When & Then
    given()
        .spec(requestSpecification)
        .pathParam("numeroNfse", numeroNfse)
        .when()
        .get("/creditos/{numeroNfse}")
        .then()
        .statusCode(200)
        .body(matchesJsonSchemaInClasspath("schemas/credito-list-schema.json"));
  }

  @Test
  @DisplayName("Deve testar performance da consulta")
  void deveTestarPerformanceDaConsulta() {
    // Given
    String numeroNfse = "7891011";
    criarCreditosParaTeste(numeroNfse);

    // When & Then
    given()
        .spec(requestSpecification)
        .pathParam("numeroNfse", numeroNfse)
        .when()
        .get("/creditos/{numeroNfse}")
        .then()
        .statusCode(200)
        .time(lessThan(2000L)); // Resposta em menos de 2 segundos
  }

  @Test
  @DisplayName("Deve lidar com caracteres especiais no número da NFS-e")
  void deveLidarComCaracteresEspeciais() {
    String numeroNfseComEspeciais = "ABC-123/2024";

    given()
        .spec(requestSpecification)
        .pathParam("numeroNfse", URLEncoder.encode(numeroNfseComEspeciais, StandardCharsets.UTF_8))
        .when()
        .get("/creditos/{numeroNfse}")
        .then()
        .statusCode(404);
  }

  private void criarCreditosParaTeste(String numeroNfse) {
    Credito credito1 =
        Credito.builder()
            .numeroCredito("123456")
            .numeroNfse(numeroNfse)
            .dataConstituicao(LocalDate.of(2024, 2, 25))
            .valorIssqn(new BigDecimal("1500.75"))
            .tipoCredito("ISSQN")
            .simplesNacional(true)
            .aliquota(new BigDecimal("5.0"))
            .valorFaturado(new BigDecimal("30000.00"))
            .valorDeducao(new BigDecimal("5000.00"))
            .baseCalculo(new BigDecimal("25000.00"))
            .build();

    Credito credito2 =
        Credito.builder()
            .numeroCredito("789012")
            .numeroNfse(numeroNfse)
            .dataConstituicao(LocalDate.of(2024, 2, 26))
            .valorIssqn(new BigDecimal("1200.50"))
            .tipoCredito("ISSQN")
            .simplesNacional(false)
            .aliquota(new BigDecimal("4.5"))
            .valorFaturado(new BigDecimal("25000.00"))
            .valorDeducao(new BigDecimal("4000.00"))
            .baseCalculo(new BigDecimal("21000.00"))
            .build();

    creditoRepository.saveAll(List.of(credito1, credito2));
  }

  private Credito criarCreditoParaTeste() {
    Credito credito =
        Credito.builder()
            .numeroCredito("654321")
            .numeroNfse("1122334")
            .dataConstituicao(LocalDate.of(2024, 1, 15))
            .valorIssqn(new BigDecimal("800.50"))
            .tipoCredito("ISSQN")
            .simplesNacional(true)
            .aliquota(new BigDecimal("3.5"))
            .valorFaturado(new BigDecimal("20000.00"))
            .valorDeducao(new BigDecimal("3000.00"))
            .baseCalculo(new BigDecimal("17000.00"))
            .build();

    return creditoRepository.save(credito);
  }

  private void verificarEventoKafka(String tipoConsulta, String valorConsulta) {
    await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(1000));

              for (ConsumerRecord<String, Object> record : records) {
                if (record.value() instanceof EventoConsultaDTO evento) {
                  assertEquals(tipoConsulta, evento.tipoConsulta());
                  assertTrue(evento.parametros().contains(valorConsulta));
                }
              }
            });
  }
}
