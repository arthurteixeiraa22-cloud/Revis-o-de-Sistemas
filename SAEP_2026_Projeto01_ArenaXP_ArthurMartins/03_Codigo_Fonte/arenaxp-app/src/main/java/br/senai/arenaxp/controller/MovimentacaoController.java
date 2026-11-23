package br.senai.arenaxp.controller;

import br.senai.arenaxp.repository.MovimentacaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MovimentacaoController {

    private final MovimentacaoRepository movimentacaoRepository;

    public MovimentacaoController(MovimentacaoRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /** RF07: histórico cronológico (mais recentes primeiro). */
    @GetMapping("/movimentacoes")
    public String historico(Model model) {
        model.addAttribute("movimentacoes", movimentacaoRepository.listarHistorico());
        return "movimentacoes";
    }
}
