package com.unipac.petshop.controller;

import com.unipac.petshop.model.LancamentoServico;
import com.unipac.petshop.repository.AnimalRepository;
import com.unipac.petshop.repository.LancamentoServicoRepository;
import com.unipac.petshop.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

// Controlador responsável por gerenciar o histórico de atendimentos e a ligação entre animais e serviços
@Controller
@RequestMapping("/lancamentos")
public class LancamentoServicoController {

    @Autowired
    private LancamentoServicoRepository lancamentoRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    // Retorna a página principal contendo a tabela de agendamentos de serviços.
    @GetMapping
    public String listarLancamentos(Model model) {
        model.addAttribute("listaLancamentos",
                lancamentoRepository.findByDataGreaterThanEqualOrderByDataAsc(LocalDate.now()));
        return "lancamentos";
    }

    // Prepara o formulário para registrar um novo atendimento.
    // Injeta a data atual automaticamente e carrega as listas de animais e serviços para os campos de seleção.
    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        LancamentoServico lancamento = new LancamentoServico();
        lancamento.setData(LocalDate.now());

        model.addAttribute("lancamento", lancamento);
        model.addAttribute("listaAnimais", animalRepository.findAll());
        model.addAttribute("listaServicos", servicoRepository.findAll());
        return "form-lancamento";
    }

    // Busca um lançamento existente pelo ID e recarrega a tela de formulário com os dados preenchidos para edição
    @GetMapping("/editar/{id}")
    public String editarLancamento(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        LancamentoServico lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        // Trava de segurança: impede a edição se a data já passou
        if (lancamento.getData().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Acesso Negado: Este agendamento já passou e virou histórico. Ele não pode mais ser alterado.");
            return "redirect:/lancamentos";
        }

        model.addAttribute("lancamento", lancamento);
        model.addAttribute("listaAnimais", animalRepository.findAll());
        model.addAttribute("listaServicos", servicoRepository.findAll());
        return "form-lancamento";
    }

    // Remove um registro de atendimento do banco de dados com base no ID fornecido
    @GetMapping("/excluir/{id}")
    public String excluirLancamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        LancamentoServico lancamento = lancamentoRepository.findById(id).orElse(null);

        // Trava de segurança: impede a exclusão se a data já passou
        if (lancamento != null && lancamento.getData().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Acesso Negado: Este agendamento já passou e virou histórico. Ele não pode ser excluído.");
            return "redirect:/lancamentos";
        }

        lancamentoRepository.deleteById(id);
        return "redirect:/lancamentos";
    }

    // Recebe os dados do formulário e aplica a regra de negócio antes de salvar no banco
    @PostMapping("/salvar")
    public String salvarLancamento(LancamentoServico lancamento, RedirectAttributes redirectAttributes) {

        // Validação anti-duplicidade: checa se já existe um registro idêntico (mesmo pet, mesmo serviço e mesma data)
        List<LancamentoServico> registrosIguais = lancamentoRepository.findByAnimalIdAndServicoIdAndData(
                lancamento.getAnimal().getId(),
                lancamento.getServico().getId(),
                lancamento.getData()
        );

        if (!registrosIguais.isEmpty()) {
            if (lancamento.getId() == null || !registrosIguais.get(0).getId().equals(lancamento.getId())) {

                // Interrompe o salvamento e devolve uma mensagem de erro temporária para a tela do usuário
                redirectAttributes.addFlashAttribute("mensagemErro",
                        "Erro: Este animal já realizou este mesmo serviço na data selecionada!");

                return "redirect:/lancamentos/novo";
            }
        }

        // Caso passe pela validação, persiste o atendimento no banco de dados e atualiza a lista
        lancamentoRepository.save(lancamento);
        return "redirect:/lancamentos";
    }
}