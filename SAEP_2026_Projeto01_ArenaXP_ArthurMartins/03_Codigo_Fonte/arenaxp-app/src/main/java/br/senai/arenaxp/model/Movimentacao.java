package br.senai.arenaxp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "tipo_movimentacao", nullable = false, length = 15)
    private TipoMovimentacao tipo;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "observacao_evento", length = 255)
    private String observacaoEvento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getObservacaoEvento() { return observacaoEvento; }
    public void setObservacaoEvento(String observacaoEvento) { this.observacaoEvento = observacaoEvento; }

    public Equipamento getEquipamento() { return equipamento; }
    public void setEquipamento(Equipamento equipamento) { this.equipamento = equipamento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
