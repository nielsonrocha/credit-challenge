package com.github.nielsonrocha.credit.repository;

import com.github.nielsonrocha.credit.entity.Credito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {

    @Query("select c from Credito c where c.numeroNfse = ?1 order by c.dataConstituicao desc")
    List<Credito> findByNumeroNfse(String numeroNfse);

    Optional<Credito> findByNumeroCredito(String numeroCredito);
}
