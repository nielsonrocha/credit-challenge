package com.github.nielsonrocha.credit.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventoConsultaDTO(
    String tipoConsulta, List<String> parametros, LocalDateTime dataHora) {}
