package com.unipac.petshop.controller;

import com.unipac.petshop.model.LancamentoServico;
import com.unipac.petshop.repository.LancamentoServicoRepository;
import com.unipac.petshop.repository.ProprietarioRepository;
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

    // Processa a exibição da tela de relatórios e executa a busca de dados caso os filtros sejam aplicados na URL
    @GetMapping
    public String exibirTelaRelatorio(
            @RequestParam(required = false) Long proprietarioId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            Model model) {

        // Carrega a lista de clientes do banco para preencher o campo de seleção (dropdown) no formulário de busca
        model.addAttribute("listaProprietarios", proprietarioRepository.findAll());

        // Verifica se o usuário preencheu todos os filtros (cliente e intervalo de datas) antes de realizar a consulta
        if (proprietarioId != null && dataInicio != null && dataFim != null) {

            // Executa a consulta cruzada no banco de dados para buscar apenas os serviços do dono e período especificados
            List<LancamentoServico> resultados = lancamentoRepository
                    .findByAnimalProprietarioIdAndDataBetween(proprietarioId, dataInicio, dataFim);

            model.addAttribute("resultados", resultados);

            // Calcula o faturamento total do período pesquisado somando o valor cobrado de todos os registros da lista
            double total = resultados.stream().mapToDouble(LancamentoServico::getValorCobrado).sum();
            model.addAttribute("valorTotal", total);
        }

        return "relatorio";
    }
}