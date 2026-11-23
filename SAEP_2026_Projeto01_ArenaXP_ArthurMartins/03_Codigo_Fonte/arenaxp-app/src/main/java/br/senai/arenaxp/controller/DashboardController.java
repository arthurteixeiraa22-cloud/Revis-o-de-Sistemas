package br.senai.arenaxp.controller;

import br.senai.arenaxp.repository.EquipamentoRepository;
import br.senai.arenaxp.repository.MovimentacaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final EquipamentoRepository equipamentoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public DashboardController(EquipamentoRepository equipamentoRepository,
                               MovimentacaoRepository movimentacaoRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalEquipamentos", equipamentoRepository.count());
        model.addAttribute("totalCriticos", equipamentoRepository.contarCriticos());
        model.addAttribute("totalMovimentacoes", movimentacaoRepository.count());
        model.addAttribute("criticos", equipamentoRepository.listarCriticos());
        return "dashboard";
    }
}
