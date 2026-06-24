package com.unipac.petshop.repository;

import com.unipac.petshop.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    Optional<Servico> findByNomeIgnoreCase(String nome);
}