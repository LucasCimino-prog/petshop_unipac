package com.unipac.petshop.controller;

import com.unipac.petshop.repository.AnimalRepository;
import com.unipac.petshop.repository.ProprietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Controlador responsável por gerenciar as rotas e operações relacionadas aos clientes (proprietários)
@Controller
@RequestMapping("/proprietarios")
public class ProprietarioController {

    @Autowired
    private ProprietarioRepository proprietarioRepository;
    @Autowired
    private AnimalRepository animalRepository;

    // Retorna a página com a tabela contendo todos os proprietários cadastrados no banco
    @GetMapping
    public String listarProprietarios(Model model) {
        model.addAttribute("listaProprietarios", proprietarioRepository.findAll());
        return "proprietarios";
    }

    // Prepara o formulário em branco para o cadastro de um novo proprietário
    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("proprietario", new com.unipac.petshop.model.Proprietario());
        return "form-proprietario";
    }

    // Recebe os dados do formulário e aplica a regra de negócio do CPF antes de salvar no banco
    @PostMapping("/salvar")
    public String salvarProprietario(com.unipac.petshop.model.Proprietario proprietario, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        // Validação anti-duplicidade: checa se o CPF submetido já pertence a outro registro no banco
        java.util.Optional<com.unipac.petshop.model.Proprietario> propExistente = proprietarioRepository.findByCpf(proprietario.getCpf());

        if (propExistente.isPresent()) {
            if (proprietario.getId() == null || !propExistente.get().getId().equals(proprietario.getId())) {

                // Interrompe o processo e envia um alerta de erro para a tela caso o CPF seja duplicado
                redirectAttributes.addFlashAttribute("mensagemErro",
                        "Erro: Já existe um cliente cadastrado com o CPF '" + proprietario.getCpf() + "'.");
                return "redirect:/proprietarios/novo";
            }
        }

        // Caso a validação passe, persiste os dados no banco e redireciona para a lista principal
        proprietarioRepository.save(proprietario);
        return "redirect:/proprietarios";
    }

    // Busca as informações de um proprietário específico pelo ID e recarrega o formulário preenchido para edição
    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        com.unipac.petshop.model.Proprietario proprietario = proprietarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        model.addAttribute("proprietario", proprietario);
        return "form-proprietario";
    }

    // Exclui fisicamente o registro de um proprietário do banco de dados através do ID
    @GetMapping("/excluir/{id}")
    public String excluirProprietario(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        java.util.List<com.unipac.petshop.model.Animal> animais = animalRepository.findByProprietarioId(id);

        if (!animais.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Erro: Não é possível excluir este proprietário pois ele possui animais ativos cadastrados.");
        } else {
            proprietarioRepository.deleteById(id);
        }
        return "redirect:/proprietarios";
    }
}