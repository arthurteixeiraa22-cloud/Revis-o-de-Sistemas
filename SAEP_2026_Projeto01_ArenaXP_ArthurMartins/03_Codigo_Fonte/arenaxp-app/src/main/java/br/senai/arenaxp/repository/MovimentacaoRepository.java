package br.senai.arenaxp.repository;

import br.senai.arenaxp.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    boolean existsByEquipamentoId(Long equipamentoId);

    /** RF07: histórico cronológico, mais recentes primeiro. */
    @Query("""
            SELECT m FROM Movimentacao m
            JOIN FETCH m.equipamento
            JOIN FETCH m.usuario
            ORDER BY m.dataHora DESC, m.id DESC
            """)
    List<Movimentacao> listarHistorico();
}
