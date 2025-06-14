package com.github.nielsonrocha.credit.service;

import com.github.nielsonrocha.credit.dto.CreditoDTO;
import java.util.List;

public interface CreditoService {

  List<CreditoDTO> findByNumeroNfse(String numeroNfse);

  CreditoDTO findByNumeroCredito(String numeroCredito);

  void enviarEventoConsulta(String tipoConsulta, String valorConsulta);
}
