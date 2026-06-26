package com.unipac.petshop.repository;

import com.unipac.petshop.model.LancamentoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LancamentoServicoRepository extends JpaRepository<LancamentoServico, Long> {
    List<LancamentoServico> findByAnimalProprietarioIdAndDataBetween(Long proprietarioId, LocalDate dataInicio, LocalDate dataFim);
    List<LancamentoServico> findByAnimalIdAndServicoIdAndData(Long animalId, Long servicoId, LocalDate data);
    List<LancamentoServico> findByDataGreaterThanEqualOrderByDataAsc(LocalDate data);
    List<LancamentoServico> findByDataLessThanOrderByDataDesc(LocalDate data);
    List<LancamentoServico> findByDataBetweenOrderByDataDesc(LocalDate dataInicio, LocalDate dataFim);
    boolean existsByAnimalId(Long animalId);
    boolean existsByServicoId(Long servicoId);
}