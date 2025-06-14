package com.github.nielsonrocha.credit.repository;

import com.github.nielsonrocha.credit.entity.Credito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {
    
}
