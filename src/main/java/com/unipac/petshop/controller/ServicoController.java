package com.unipac.petshop.controller;

import com.unipac.petshop.model.Servico;
import com.unipac.petshop.repository.LancamentoServicoRepository;
import com.unipac.petshop.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

// Controlador responsável por gerenciar as rotas e o catálogo de serviços oferecidos pelo pet shop
@Controller
@RequestMapping("/servicos")
public class ServicoController {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private LancamentoServicoRepository lancamentoRepository;

    // Retorna a página contendo a tabela com todos os serviços disponíveis cadastrados no banco
    @GetMapping
    public String listarServicos(Model model) {
        model.addAttribute("listaServicos", servicoRepository.findAll());
        return "servicos";
    }

    // Prepara e exibe o formulário em branco para a criação de um novo serviço
    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("servico", new Servico());
        return "form-servico";
    }

    // Recebe os dados do formulário e aplica a regra de negócio para evitar serviços duplicados antes de salvar
    @PostMapping("/salvar")
    public String salvarServico(Servico servico, RedirectAttributes redirectAttributes) {

        // Validação anti-duplicidade: verifica no banco de dados se já existe um serviço com o mesmo nome (ignorando maiúsculas e minúsculas)
        java.util.Optional<Servico> servicoExistente = servicoRepository.findByNomeIgnoreCase(servico.getNome());

        if (servicoExistente.isPresent()) {
            if (servico.getId() == null || !servicoExistente.get().getId().equals(servico.getId())) {

                // Bloqueia o cadastro e retorna uma mensagem de alerta temporária para a interface caso o nome do serviço já esteja em uso
                redirectAttributes.addFlashAttribute("mensagemErro",
                        "Erro: Já existe um serviço cadastrado com o nome '" + servico.getNome() + "'.");

                return "redirect:/servicos/novo";
            }
        }

        // Caso o nome seja único, persiste as informações do serviço no banco de dados e atualiza a listagem
        servicoRepository.save(servico);
        return "redirect:/servicos";
    }

    // Busca os dados de um serviço específico através do ID e recarrega a tela de formulário com as informações preenchidas para edição
    @GetMapping("/editar/{id}")
    public String editarServico (@PathVariable Long id, Model model){
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("servico", servico);
        return "form-servico";
    }

    // Remove fisicamente o registro de um serviço do banco de dados utilizando o seu ID
    @GetMapping("/excluir/{id}")
    public String excluirServico(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        if (lancamentoRepository.existsByServicoId(id)) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Erro: Não é possível excluir este serviço pois ele já possui lançamentos no histórico financeiro.");
        } else {
            servicoRepository.deleteById(id);
        }
        return "redirect:/servicos";
    }
}