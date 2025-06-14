package com.github.nielsonrocha.credit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.github.nielsonrocha.credit.dto.CreditoDTO;
import com.github.nielsonrocha.credit.dto.EventoConsultaDTO;
import com.github.nielsonrocha.credit.entity.Credito;
import com.github.nielsonrocha.credit.exception.CreditoNotFoundException;
import com.github.nielsonrocha.credit.mapper.CreditoMapper;
import com.github.nielsonrocha.credit.repository.CreditoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CreditoServiceTest {

  @Mock private CreditoRepository creditoRepository;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @Mock private CreditoMapper creditoMapper;

  @InjectMocks private CreditoService creditoService;

  private final String TOPICO = "credito-topic";
  private Credito credito;
  private CreditoDTO creditoDTO;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(creditoService, "topico", TOPICO);

    credito = new Credito();
    credito.setNumeroCredito("123456");
    credito.setNumeroNfse("123456789");

    creditoDTO =
        new CreditoDTO(
            "123456",
            "123456789",
            LocalDate.now(),
            new BigDecimal("100.00"),
            "'ISSQN'",
            true,
            new BigDecimal("5.00"),
            new BigDecimal("2000.00"),
            new BigDecimal("0.00"),
            new BigDecimal("2000.00"));
  }

  @Test
  void findByNumeroNfse_deveRetornarListaDeCreditos_quandoEncontrados() {
    String numeroNfse = "123456789";
    List<Credito> creditos = Collections.singletonList(credito);

    when(creditoRepository.findByNumeroNfse(numeroNfse)).thenReturn(creditos);
    when(creditoMapper.toCreditoDTO(any(Credito.class))).thenReturn(creditoDTO);

    List<CreditoDTO> resultado = creditoService.findByNumeroNfse(numeroNfse);

    assertNotNull(resultado);
    assertEquals(1, resultado.size());
    assertEquals(creditoDTO, resultado.getFirst());

    verify(creditoRepository).findByNumeroNfse(numeroNfse);
    verify(creditoMapper).toCreditoDTO(credito);
    verify(kafkaTemplate).send(eq(TOPICO), any(EventoConsultaDTO.class));
  }

  @Test
  void findByNumeroNfse_deveLancarException_quandoNaoEncontrados() {
    String numeroNfse = "NFSE-123";
    when(creditoRepository.findByNumeroNfse(numeroNfse)).thenReturn(Collections.emptyList());

    CreditoNotFoundException exception =
        assertThrows(
            CreditoNotFoundException.class, () -> creditoService.findByNumeroNfse(numeroNfse));

    assertEquals("Nenhum crédito encontrado para a NFS-e: " + numeroNfse, exception.getMessage());
    verify(creditoRepository).findByNumeroNfse(numeroNfse);
    verify(kafkaTemplate, never()).send(anyString(), any());
  }

  @Test
  void findByNumeroCredito_deveRetornarCredito_quandoEncontrado() {
    String numeroCredito = "123456";
    when(creditoRepository.findByNumeroCredito(numeroCredito)).thenReturn(Optional.of(credito));
    when(creditoMapper.toCreditoDTO(credito)).thenReturn(creditoDTO);

    CreditoDTO resultado = creditoService.findByNumeroCredito(numeroCredito);

    assertNotNull(resultado);
    assertEquals(creditoDTO, resultado);

    verify(creditoRepository).findByNumeroCredito(numeroCredito);
    verify(creditoMapper).toCreditoDTO(credito);
    verify(kafkaTemplate).send(eq(TOPICO), any(EventoConsultaDTO.class));
  }

  @Test
  void findByNumeroCredito_deveLancarException_quandoNaoEncontrado() {
    String numeroCredito = "123456";
    when(creditoRepository.findByNumeroCredito(numeroCredito)).thenReturn(Optional.empty());

    CreditoNotFoundException exception =
        assertThrows(
            CreditoNotFoundException.class,
            () -> creditoService.findByNumeroCredito(numeroCredito));

    assertEquals("Crédito não encontrado: " + numeroCredito, exception.getMessage());
    verify(creditoRepository).findByNumeroCredito(numeroCredito);
    verify(kafkaTemplate, never()).send(anyString(), any());
  }

  @Test
  void enviarEventoConsulta_deveCapturarExcecao_quandoErroNoEnvio() {
    String numeroCredito = "123456";
    when(creditoRepository.findByNumeroCredito(numeroCredito)).thenReturn(Optional.of(credito));
    when(creditoMapper.toCreditoDTO(credito)).thenReturn(creditoDTO);
    when(kafkaTemplate.send(eq(TOPICO), any(EventoConsultaDTO.class)))
        .thenThrow(new RuntimeException("Erro Kafka"));

    CreditoDTO resultado = creditoService.findByNumeroCredito(numeroCredito);

    assertNotNull(resultado);
    assertEquals(creditoDTO, resultado);

    verify(creditoRepository).findByNumeroCredito(numeroCredito);
    verify(creditoMapper).toCreditoDTO(credito);
    verify(kafkaTemplate).send(eq(TOPICO), any(EventoConsultaDTO.class));
  }
}
