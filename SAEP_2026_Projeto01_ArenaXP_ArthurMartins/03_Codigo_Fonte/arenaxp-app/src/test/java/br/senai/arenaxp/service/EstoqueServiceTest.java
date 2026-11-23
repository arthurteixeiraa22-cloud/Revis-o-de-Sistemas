package br.senai.arenaxp.service;

import br.senai.arenaxp.model.Equipamento;
import br.senai.arenaxp.model.Movimentacao;
import br.senai.arenaxp.model.TipoMovimentacao;
import br.senai.arenaxp.model.Usuario;
import br.senai.arenaxp.repository.EquipamentoRepository;
import br.senai.arenaxp.repository.MovimentacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EquipamentoRepository equipamentoRepository;
    @Mock
    private MovimentacaoRepository movimentacaoRepository;
    @InjectMocks
    private EstoqueService service;

    private Equipamento equipamento;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        equipamento = new Equipamento();
        equipamento.setId(1L);
        equipamento.setNome("Headset HyperX Cloud III");
        equipamento.setQuantidadeAtual(4);
        equipamento.setEstoqueMinimo(3);

        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Carlos");
    }

    @Test
    void saidaMaiorQueSaldo_deveSerBloqueadaESemAlterarSaldo() {
        when(equipamentoRepository.buscarParaAtualizar(1L)).thenReturn(Optional.of(equipamento));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                service.registrar(1L, TipoMovimentacao.SAIDA, 5, null, "Teste", usuario));

        assertTrue(ex.getMessage().contains("Saída bloqueada"));
        assertEquals(4, equipamento.getQuantidadeAtual());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    void saidaValida_deveDescontarSaldoEVincularUsuario() {
        when(equipamentoRepository.buscarParaAtualizar(1L)).thenReturn(Optional.of(equipamento));

        Movimentacao mov = service.registrar(1L, TipoMovimentacao.SAIDA, 1, null, "  Copa FPS  ", usuario);

        assertEquals(3, equipamento.getQuantidadeAtual());
        assertSame(usuario, mov.getUsuario());
        assertEquals("Copa FPS", mov.getObservacaoEvento());
        assertNotNull(mov.getDataHora());
        verify(movimentacaoRepository).save(mov);
    }

    @Test
    void saidaQueAtingeOMinimo_deveMarcarEquipamentoComoCritico() {
        when(equipamentoRepository.buscarParaAtualizar(1L)).thenReturn(Optional.of(equipamento));

        assertFalse(equipamento.isCritico());
        service.registrar(1L, TipoMovimentacao.SAIDA, 1, null, null, usuario);

        assertEquals(3, equipamento.getQuantidadeAtual());
        assertTrue(equipamento.isCritico());
    }

    @Test
    void saidaDeTodoOSaldo_deveSerPermitida() {
        when(equipamentoRepository.buscarParaAtualizar(1L)).thenReturn(Optional.of(equipamento));

        service.registrar(1L, TipoMovimentacao.SAIDA, 4, null, null, usuario);

        assertEquals(0, equipamento.getQuantidadeAtual());
    }

    @Test
    void entrada_deveSomarAoSaldo() {
        when(equipamentoRepository.buscarParaAtualizar(1L)).thenReturn(Optional.of(equipamento));

        service.registrar(1L, TipoMovimentacao.ENTRADA, 10, null, null, usuario);

        assertEquals(14, equipamento.getQuantidadeAtual());
        assertFalse(equipamento.isCritico());
    }

    @Test
    void quantidadeZeroOuNegativa_deveSerRejeitada() {
        assertThrows(RegraNegocioException.class, () ->
                service.registrar(1L, TipoMovimentacao.ENTRADA, 0, null, null, usuario));
        assertThrows(RegraNegocioException.class, () ->
                service.registrar(1L, TipoMovimentacao.SAIDA, -2, null, null, usuario));
        verify(movimentacaoRepository, never()).save(any());
    }
}
