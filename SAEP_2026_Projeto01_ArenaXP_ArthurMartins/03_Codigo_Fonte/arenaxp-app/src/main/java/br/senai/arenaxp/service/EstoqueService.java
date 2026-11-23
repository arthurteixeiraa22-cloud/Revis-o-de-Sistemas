package br.senai.arenaxp.service;

import br.senai.arenaxp.model.Equipamento;
import br.senai.arenaxp.model.Movimentacao;
import br.senai.arenaxp.model.TipoMovimentacao;
import br.senai.arenaxp.model.Usuario;
import br.senai.arenaxp.repository.EquipamentoRepository;
import br.senai.arenaxp.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EstoqueService {

    private final EquipamentoRepository equipamentoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public EstoqueService(EquipamentoRepository equipamentoRepository,
                          MovimentacaoRepository movimentacaoRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * RF05 + RF06: registra ENTRADA ou SAIDA, atualiza o saldo e vincula o usuário logado.
     * REGRA CRÍTICA: uma SAIDA nunca pode ser maior que a quantidade_atual.
     */
    @Transactional
    public Movimentacao registrar(Long equipamentoId, TipoMovimentacao tipo, Integer quantidade,
                                  LocalDateTime dataHora, String observacao, Usuario usuario) {

        if (equipamentoId == null) {
            throw new RegraNegocioException("Selecione um equipamento.");
        }
        if (tipo == null) {
            throw new RegraNegocioException("Selecione o tipo da movimentação (ENTRADA ou SAIDA).");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }

        Equipamento equipamento = equipamentoRepository.buscarParaAtualizar(equipamentoId)
                .orElseThrow(() -> new RegraNegocioException("Equipamento não encontrado."));

        int saldoAtual = equipamento.getQuantidadeAtual();

        if (tipo == TipoMovimentacao.SAIDA) {
            if (quantidade > saldoAtual) {
                throw new RegraNegocioException(
                        "Saída bloqueada: quantidade solicitada (" + quantidade
                                + ") maior que o saldo disponível (" + saldoAtual
                                + ") de \"" + equipamento.getNome() + "\".");
            }
            equipamento.setQuantidadeAtual(saldoAtual - quantidade);
        } else {
            equipamento.setQuantidadeAtual(saldoAtual + quantidade);
        }
        equipamentoRepository.save(equipamento);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setEquipamento(equipamento);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipo(tipo);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setDataHora(dataHora != null ? dataHora : LocalDateTime.now());
        String obs = observacao == null ? null : observacao.trim();
        movimentacao.setObservacaoEvento(obs == null || obs.isEmpty() ? null : obs);
        movimentacaoRepository.save(movimentacao);

        return movimentacao;
    }
}
