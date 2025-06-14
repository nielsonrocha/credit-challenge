package com.github.nielsonrocha.credit.mapper;

import com.github.nielsonrocha.credit.dto.CreditoDTO;
import com.github.nielsonrocha.credit.entity.Credito;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditoMapper {

    CreditoDTO toCreditoDTO(Credito credito);
}
