package br.senai.arenaxp.repository;

import br.senai.arenaxp.model.Equipamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    /** RF04: ordenação alfabética crescente (A-Z) pelo nome. */
    List<Equipamento> findAllByOrderByNomeAsc();

    /**
     * RF03: busca por nome ou código de patrimônio (case-insensitive) + filtro opcional por categoria.
     * Parâmetros vazios ("") significam "sem filtro".
     */
    @Query("""
            SELECT e FROM Equipamento e
            WHERE (LOWER(e.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(e.codigoPatrimonio) LIKE LOWER(CONCAT('%', :busca, '%')))
              AND (:categoria = '' OR e.categoria = :categoria)
            ORDER BY e.nome ASC
            """)
    List<Equipamento> pesquisar(@Param("busca") String busca, @Param("categoria") String categoria);

    boolean existsByCodigoPatrimonioIgnoreCase(String codigoPatrimonio);

    boolean existsByCodigoPatrimonioIgnoreCaseAndIdNot(String codigoPatrimonio, Long id);

    @Query("SELECT COUNT(e) FROM Equipamento e WHERE e.quantidadeAtual <= e.estoqueMinimo")
    long contarCriticos();

    @Query("SELECT e FROM Equipamento e WHERE e.quantidadeAtual <= e.estoqueMinimo ORDER BY e.nome ASC")
    List<Equipamento> listarCriticos();

    /** Bloqueio pessimista para evitar saldo inconsistente em saídas simultâneas. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Equipamento e WHERE e.id = :id")
    Optional<Equipamento> buscarParaAtualizar(@Param("id") Long id);
}
