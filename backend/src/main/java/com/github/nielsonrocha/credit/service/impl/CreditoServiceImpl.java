package com.github.nielsonrocha.credit.service.impl;

import com.github.nielsonrocha.credit.dto.CreditoDTO;
import com.github.nielsonrocha.credit.dto.EventoConsultaDTO;
import com.github.nielsonrocha.credit.entity.Credito;
import com.github.nielsonrocha.credit.exception.CreditoNotFoundException;
import com.github.nielsonrocha.credit.mapper.CreditoMapper;
import com.github.nielsonrocha.credit.repository.CreditoRepository;
import com.github.nielsonrocha.credit.service.CreditoService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditoServiceImpl implements CreditoService {

  private final CreditoRepository creditoRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final CreditoMapper creditoMapper;

  @Value("${app.credito-topic}")
  private String topico;

  public List<CreditoDTO> findByNumeroNfse(String numeroNfse) {
    log.info("Consultando créditos para NFS-e: {}", numeroNfse);

    List<Credito> creditos = creditoRepository.findByNumeroNfse(numeroNfse);

    if (creditos.isEmpty()) {
      throw new CreditoNotFoundException("Nenhum crédito encontrado para a NFS-e: " + numeroNfse);
    }

    return creditos.stream().map(creditoMapper::toCreditoDTO).collect(Collectors.toList());
  }

  public CreditoDTO findByNumeroCredito(String numeroCredito) {
    log.info("Consultando crédito por número: {}", numeroCredito);

    Credito credito =
        creditoRepository
            .findByNumeroCredito(numeroCredito)
            .orElseThrow(
                () -> new CreditoNotFoundException("Crédito não encontrado: " + numeroCredito));

    return creditoMapper.toCreditoDTO(credito);
  }

  @Async
  public void enviarEventoConsulta(String tipoConsulta, String valorConsulta) {
    try {
      var evento =
          new EventoConsultaDTO(
              tipoConsulta, Collections.singletonList(valorConsulta), LocalDateTime.now());
      kafkaTemplate.send(topico, evento);
      log.info("Evento de consulta enviado: {} - {}", tipoConsulta, valorConsulta);
    } catch (Exception e) {
      log.error("Erro ao enviar evento para Kafka", e);
    }
  }
}
