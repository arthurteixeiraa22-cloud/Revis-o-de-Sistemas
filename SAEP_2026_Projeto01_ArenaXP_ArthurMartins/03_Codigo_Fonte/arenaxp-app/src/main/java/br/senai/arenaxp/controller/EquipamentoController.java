package br.senai.arenaxp.controller;

import br.senai.arenaxp.model.Equipamento;
import br.senai.arenaxp.repository.EquipamentoRepository;
import br.senai.arenaxp.repository.MovimentacaoRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoRepository equipamentoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public EquipamentoController(EquipamentoRepository equipamentoRepository,
                                 MovimentacaoRepository movimentacaoRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /** RF02 + RF03: listagem com busca (nome/patrimônio) e filtro por categoria. */
    @GetMapping
    public String listar(@RequestParam(required = false, defaultValue = "") String busca,
                         @RequestParam(required = false, defaultValue = "") String categoria,
                         Model model) {
        model.addAttribute("equipamentos", equipamentoRepository.pesquisar(busca.trim(), categoria.trim()));
        model.addAttribute("busca", busca);
        model.addAttribute("categoriaSelecionada", categoria);
        return "equipamentos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Equipamento equipamento = new Equipamento();
        equipamento.setQuantidadeAtual(0);
        equipamento.setEstoqueMinimo(0);
        model.addAttribute("equipamento", equipamento);
        return "equipamento-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return equipamentoRepository.findById(id)
                .map(e -> {
                    model.addAttribute("equipamento", e);
                    return "equipamento-form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Equipamento não encontrado.");
                    return "redirect:/equipamentos";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("equipamento") Equipamento equipamento,
                         BindingResult result, RedirectAttributes ra) {

        if (equipamento.getCodigoPatrimonio() != null) {
            equipamento.setCodigoPatrimonio(equipamento.getCodigoPatrimonio().trim());
        }
        if (equipamento.getNome() != null) {
            equipamento.setNome(equipamento.getNome().trim());
        }

        String codigo = equipamento.getCodigoPatrimonio();
        if (codigo != null && !codigo.isEmpty() && !result.hasFieldErrors("codigoPatrimonio")) {
            Long id = equipamento.getId();
            boolean duplicado = (id == null)
                    ? equipamentoRepository.existsByCodigoPatrimonioIgnoreCase(codigo)
                    : equipamentoRepository.existsByCodigoPatrimonioIgnoreCaseAndIdNot(codigo, id);
            if (duplicado) {
                result.rejectValue("codigoPatrimonio", "duplicado",
                        "Já existe um equipamento com este código de patrimônio.");
            }
        }

        if (result.hasErrors()) {
            return "equipamento-form";
        }

        boolean novo = equipamento.getId() == null;
        try {
            equipamentoRepository.save(equipamento);
            ra.addFlashAttribute("sucesso", novo
                    ? "Equipamento cadastrado com sucesso!"
                    : "Equipamento atualizado com sucesso!");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("erro", "Não foi possível salvar: dados inválidos ou código de patrimônio já existente.");
        }
        return "redirect:/equipamentos";
    }

    /** RNF05: impede exclusão de equipamento com movimentações, sem erro 500. */
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        if (!equipamentoRepository.existsById(id)) {
            ra.addFlashAttribute("erro", "Equipamento não encontrado.");
            return "redirect:/equipamentos";
        }
        if (movimentacaoRepository.existsByEquipamentoId(id)) {
            ra.addFlashAttribute("erro",
                    "Não é possível excluir: este equipamento possui movimentações registradas.");
            return "redirect:/equipamentos";
        }
        try {
            equipamentoRepository.deleteById(id);
            ra.addFlashAttribute("sucesso", "Equipamento excluído com sucesso!");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("erro",
                    "Não é possível excluir: este equipamento está vinculado a outros registros.");
        }
        return "redirect:/equipamentos";
    }
}
