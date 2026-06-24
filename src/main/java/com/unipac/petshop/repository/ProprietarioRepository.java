package com.unipac.petshop.repository;

import com.unipac.petshop.model.Proprietario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {

    Optional<Proprietario> findByCpf(String cpf);
}