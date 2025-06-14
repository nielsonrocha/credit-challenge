package com.github.nielsonrocha.credit.controller;

import com.github.nielsonrocha.credit.dto.CreditoDTO;
import com.github.nielsonrocha.credit.service.CreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/creditos")
@Tag(name = "Créditos", description = "API para consulta de créditos constituídos")
@RequiredArgsConstructor
public class CreditoController {

  private final CreditoService creditoService;

  @GetMapping("/{numeroNfse}")
  @Operation(
      summary = "Buscar créditos por NFS-e",
      description = "Retorna uma lista de créditos constituídos por número da NFS-e")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Créditos encontrados com sucesso"),
    @ApiResponse(responseCode = "404", description = "Nenhum crédito encontrado"),
  })
  public ResponseEntity<List<CreditoDTO>> getCreditosByNfse(
      @Parameter(description = "Número da NFS-e", required = true) @PathVariable
          String numeroNfse) {
    var resultado = creditoService.findByNumeroNfse(numeroNfse);
    creditoService.enviarEventoConsulta("NFSE", numeroNfse);
    return ResponseEntity.ok(resultado);
  }

  @GetMapping("/credito/{numeroCredito}")
  @Operation(
      summary = "Buscar detalhe do crédito por número do crédito",
      description = "Retorna os detalhes de um crédito constituído específico")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Créditos encontrados"),
    @ApiResponse(responseCode = "404", description = "Nenhum crédito encontrado")
  })
  public ResponseEntity<CreditoDTO> getCreditoByNumero(
      @Parameter(description = "Número do crédito constituído", required = true) @PathVariable
          String numeroCredito) {
    var resultado = creditoService.findByNumeroCredito(numeroCredito);
    creditoService.enviarEventoConsulta("CREDITO", numeroCredito);
    return ResponseEntity.ok(resultado);
  }
}
