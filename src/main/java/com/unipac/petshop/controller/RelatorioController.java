package com.unipac.petshop.controller;

import com.unipac.petshop.model.LancamentoServico;
import com.unipac.petshop.repository.LancamentoServicoRepository;
import com.unipac.petshop.repository.ProprietarioRepository;
import com.unipac.petshop.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

// Controlador responsável por processar e exibir relatórios financeiros e de histórico de atendimentos
@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private LancamentoServicoRepository lancamentoRepository;

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    // Processa a exibição da tela de relatórios e executa a busca de dados caso os filtros sejam aplicados na URL
    @GetMapping
    public String exibirTelaRelatorio(
            @RequestParam(required = false) Long proprietarioId,
            @RequestParam(required = false) Long servicoId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            Model model) {

        // Carrega a lista de clientes do banco para preencher o campo de seleção (dropdown) no formulário de busca
        model.addAttribute("listaProprietarios", proprietarioRepository.findAll());

        // Carrega serviços para o filtro
        model.addAttribute("listaServicos", servicoRepository.findAll());

        // Verifica se O QUALQUER um dos filtros foi preenchido
        boolean fezBusca = (proprietarioId != null || servicoId != null || (dataInicio != null && dataFim != null));

        if (fezBusca) {
            List<LancamentoServico> resultados;

            // 1. Filtro Base de Data: Tem data de início e fim?
            if (dataInicio != null && dataFim != null) {
                resultados = lancamentoRepository.findByDataBetweenOrderByDataDesc(dataInicio, dataFim);
            } else {
                // Se NÃO tem data, busca todo o lucro histórico (de ontem para trás)
                resultados = lancamentoRepository.findByDataLessThanOrderByDataDesc(LocalDate.now());
            }

            // 2. Filtro de Cliente (Aplica apenas se o usuário selecionou alguém)
            if (proprietarioId != null) {
                resultados = resultados.stream()
                        .filter(l -> l.getAnimal() != null && l.getAnimal().getProprietario().getId().equals(proprietarioId))
                        .toList();
            }

            // 3. Filtro de Serviço (Aplica apenas se o usuário escolheu um serviço específico)
            if (servicoId != null) {
                resultados = resultados.stream()
                        .filter(l -> l.getServico() != null && l.getServico().getId().equals(servicoId))
                        .toList();
            }

            model.addAttribute("resultados", resultados);

            // Soma total do cenário filtrado
            double total = resultados.stream().mapToDouble(LancamentoServico::getValorCobrado).sum();
            model.addAttribute("valorTotal", total);
            model.addAttribute("modoBusca", true);

        } else {
            // CENA PADRÃO: Visão Geral de todo o histórico sem filtros
            List<LancamentoServico> historico = lancamentoRepository.findByDataLessThanOrderByDataDesc(LocalDate.now());
            model.addAttribute("historicoPadrao", historico);
            model.addAttribute("modoBusca", false);
        }

        return "relatorio";
    }
}