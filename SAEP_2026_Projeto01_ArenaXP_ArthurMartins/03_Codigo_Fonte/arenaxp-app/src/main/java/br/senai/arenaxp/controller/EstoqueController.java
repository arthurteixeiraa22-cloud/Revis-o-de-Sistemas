package br.senai.arenaxp.controller;

import br.senai.arenaxp.model.Equipamento;
import br.senai.arenaxp.model.Movimentacao;
import br.senai.arenaxp.model.TipoMovimentacao;
import br.senai.arenaxp.model.Usuario;
import br.senai.arenaxp.repository.EquipamentoRepository;
import br.senai.arenaxp.repository.UsuarioRepository;
import br.senai.arenaxp.service.EstoqueService;
import br.senai.arenaxp.service.RegraNegocioException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstoqueService estoqueService;

    public EstoqueController(EquipamentoRepository equipamentoRepository,
                             UsuarioRepository usuarioRepository,
                             EstoqueService estoqueService) {
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estoqueService = estoqueService;
    }

    /** RF04: listagem em ordem alfabética crescente (A-Z) pelo nome. */
    @GetMapping
    public String estoque(@RequestParam(required = false) Long equipamentoId, Model model) {
        model.addAttribute("equipamentos", equipamentoRepository.findAllByOrderByNomeAsc());
        model.addAttribute("tipos", TipoMovimentacao.values());
        model.addAttribute("equipamentoSelecionado", equipamentoId);
        return "estoque";
    }

    /** RF05 + RF06: registra a movimentação vinculada ao usuário logado. */
    @PostMapping("/movimentar")
    public String movimentar(@RequestParam(required = false) Long equipamentoId,
                             @RequestParam(required = false) TipoMovimentacao tipo,
                             @RequestParam(required = false) Integer quantidade,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora,
                             @RequestParam(required = false) String observacao,
                             Authentication auth,
                             RedirectAttributes ra) {
        try {
            Usuario usuario = usuarioRepository.findByEmailIgnoreCase(auth.getName())
                    .orElseThrow(() -> new RegraNegocioException("Usuário logado não encontrado."));

            Movimentacao mov = estoqueService.registrar(equipamentoId, tipo, quantidade, dataHora, observacao, usuario);
            Equipamento eq = mov.getEquipamento();

            ra.addFlashAttribute("sucesso", "Movimentação de " + mov.getTipo() + " registrada: "
                    + mov.getQuantidade() + " un. de \"" + eq.getNome() + "\". Novo saldo: " + eq.getQuantidadeAtual() + ".");

            if (mov.getTipo() == TipoMovimentacao.SAIDA && eq.isCritico()) {
                ra.addFlashAttribute("alerta", "ESTOQUE CRÍTICO / ABAIXO DO MÍNIMO: \"" + eq.getNome()
                        + "\" está com " + eq.getQuantidadeAtual() + " un. (mínimo: " + eq.getEstoqueMinimo() + ").");
            }
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            if (equipamentoId != null) {
                ra.addAttribute("equipamentoId", equipamentoId);
            }
        }
        return "redirect:/estoque";
    }
}
