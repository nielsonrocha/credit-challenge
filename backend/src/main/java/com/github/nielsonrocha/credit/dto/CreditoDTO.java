package com.github.nielsonrocha.credit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditoDTO(
    String numeroCredito,
    String numeroNfse,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dataConstituicao,
    BigDecimal valorIssqn,
    String tipoCredito,
    Boolean simplesNacional,
    BigDecimal aliquota,
    BigDecimal valorFaturado,
    BigDecimal valorDeducao,
    BigDecimal baseCalculo) {}
