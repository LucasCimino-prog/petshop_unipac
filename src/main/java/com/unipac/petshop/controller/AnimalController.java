package com.unipac.petshop.controller;

import com.unipac.petshop.model.Animal;
import com.unipac.petshop.repository.AnimalRepository;
import com.unipac.petshop.repository.LancamentoServicoRepository;
import com.unipac.petshop.repository.ProprietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Controlador responsável por gerenciar as rotas e operações relacionadas aos animais
@Controller
@RequestMapping("/animais")
public class AnimalController {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private LancamentoServicoRepository lancamentoRepository;

    @Autowired
    private ProprietarioRepository proprietarioRepository;
    @Autowired
    private LancamentoServicoRepository lancamentoServicoRepository;

    // Retorna a página com a tabela contendo todos os animais cadastrados no banco
    @GetMapping
    public String listarAnimais(Model model) {
        model.addAttribute("listaAnimais", animalRepository.findAll());
        return "animais";
    }

    // Prepara o formulário em branco para um novo cadastro, enviando também a lista de proprietários para preencher a seleção
    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("animal", new Animal());
        model.addAttribute("listaProprietarios", proprietarioRepository.findAll());
        return "form-animal";
    }

    // Recebe os dados do formulário para salvar no banco, com regras de negócio e upload de arquivo
    @PostMapping("/salvar")
    public String salvarAnimal(Animal animal,
                               @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        // Validação anti-duplicidade: impede que um cliente registre dois pets com exatamente o mesmo nome
        java.util.Optional<Animal> animalExistente = animalRepository.findByNomeIgnoreCaseAndProprietarioId(
                animal.getNome(), animal.getProprietario().getId());

        if (animalExistente.isPresent()) {
            if (animal.getId() == null || !animalExistente.get().getId().equals(animal.getId())) {
                redirectAttributes.addFlashAttribute("mensagemErro",
                        "Erro: Este cliente já possui um pet cadastrado com o nome '" + animal.getNome() + "'.");
                return "redirect:/animais/novo";
            }
        }

        try {
            // Lógica de upload da foto: cria a pasta local (se necessário), gera um nome único baseado no horário atual e salva o arquivo físico
            if (file != null && !file.isEmpty()) {
                String pastaUploads = "uploads/";
                java.io.File dir = new java.io.File(pastaUploads);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.nio.file.Path caminho = java.nio.file.Paths.get(pastaUploads + nomeArquivo);
                java.nio.file.Files.write(caminho, file.getBytes());
                animal.setFotoUrl(nomeArquivo);
            }

            // Persiste o registro no banco de dados e retorna para a listagem
            animalRepository.save(animal);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/animais";
    }

    // Busca as informações de um animal específico pelo ID e recarrega o formulário preenchido para edição
    @GetMapping("/editar/{id}")
    public String editarAnimal(@PathVariable Long id, Model model) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        model.addAttribute("animal", animal);
        model.addAttribute("listaProprietarios", proprietarioRepository.findAll());
        return "form-animal";
    }

    // Exclui o registro de um animal no banco de dados através do ID fornecido na URL
    @GetMapping("/excluir/{id}")
    public String excluirAnimal(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        // Verifica se existem serviços prestados para este animal
        if (lancamentoRepository.existsByAnimalId(id)) { // Precisa injetar o lancamentoRepository no AnimalController!
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Erro: Não é possível excluir este pet pois ele já possui serviços registrados no histórico.");
        } else {
            animalRepository.deleteById(id);
        }
        return "redirect:/animais";
    }
}