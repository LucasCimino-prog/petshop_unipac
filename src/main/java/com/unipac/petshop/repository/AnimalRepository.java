package com.unipac.petshop.repository;

import com.unipac.petshop.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findByNomeIgnoreCaseAndProprietarioId(String nome, Long proprietarioId);
    List<Animal> findByProprietarioId(Long proprietarioId);
}