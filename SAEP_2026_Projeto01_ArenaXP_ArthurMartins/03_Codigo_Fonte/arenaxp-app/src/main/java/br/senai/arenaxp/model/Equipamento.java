package br.senai.arenaxp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "equipamentos")
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o código de patrimônio.")
    @Size(max = 30, message = "O código de patrimônio deve ter no máximo 30 caracteres.")
    @Column(name = "codigo_patrimonio", nullable = false, unique = true, length = 30)
    private String codigoPatrimonio;

    @NotBlank(message = "Informe o nome do equipamento.")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Selecione a categoria.")
    @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres.")
    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @NotBlank(message = "Informe a especificação técnica.")
    @Size(max = 255, message = "A especificação deve ter no máximo 255 caracteres.")
    @Column(name = "especificacao_tecnica", nullable = false, length = 255)
    private String especificacaoTecnica;

    @NotNull(message = "Informe a quantidade atual.")
    @Min(value = 0, message = "A quantidade atual não pode ser negativa.")
    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual;

    @NotNull(message = "Informe o estoque mínimo.")
    @Min(value = 0, message = "O estoque mínimo não pode ser negativo.")
    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    /** Regra de alerta: saldo menor ou igual ao estoque mínimo. */
    @Transient
    public boolean isCritico() {
        return quantidadeAtual != null && estoqueMinimo != null && quantidadeAtual <= estoqueMinimo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoPatrimonio() { return codigoPatrimonio; }
    public void setCodigoPatrimonio(String codigoPatrimonio) { this.codigoPatrimonio = codigoPatrimonio; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEspecificacaoTecnica() { return especificacaoTecnica; }
    public void setEspecificacaoTecnica(String especificacaoTecnica) { this.especificacaoTecnica = especificacaoTecnica; }

    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
}
