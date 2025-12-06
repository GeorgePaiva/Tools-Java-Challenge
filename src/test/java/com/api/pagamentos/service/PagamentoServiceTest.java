package com.api.pagamentos.service;

import com.api.pagamentos.dto.DescricaoDTO;
import com.api.pagamentos.dto.PagamentoRequest;
import com.api.pagamentos.dto.PagamentoResponse;
import com.api.pagamentos.dto.TransacaoDTO;
import com.api.pagamentos.entity.DescricaoEmbeddable;
import com.api.pagamentos.entity.FormaPagamentoEmbeddable;
import com.api.pagamentos.entity.PagamentoEntity;
import com.api.pagamentos.entity.enums.StatusTransacao;
import com.api.pagamentos.entity.enums.TipoPagamento;
import com.api.pagamentos.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    @BeforeEach
    void setUp() {
        when(pagamentoRepository.save(any(PagamentoEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createPayment_withValidEvenCard_masksCardAndAuthorizesPayment() {
        PagamentoRequest request = buildPagamentoRequest("1234567890123456", TipoPagamento.AVISTA.getValue());

        PagamentoResponse response = pagamentoService.createPayment(request);

        assertThat(response.getTransacao().getCartao()).isEqualTo("1234********3456");
        assertThat(response.getTransacao().getDescricao().getStatus())
                .isEqualTo(StatusTransacao.AUTORIZADO.getValue());
        assertThat(response.getTransacao().getDescricao().getNsu()).isNotBlank();
        assertThat(response.getTransacao().getDescricao().getCodigoAutorizacao()).isNotBlank();
    }

    @Test
    void createPayment_withInvalidCard_throwsException() {
        PagamentoRequest request = buildPagamentoRequest("123", TipoPagamento.AVISTA.getValue());

        assertThatThrownBy(() -> pagamentoService.createPayment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cartao inválido");
    }

    @Test
    void refund_updatesStatusToCancelled() {
        PagamentoEntity existingEntity = buildPagamentoEntity(StatusTransacao.AUTORIZADO.getValue());
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(existingEntity));

        PagamentoResponse response = pagamentoService.refund(1L);

        assertThat(response.getTransacao().getDescricao().getStatus())
                .isEqualTo(StatusTransacao.CANCELADO.getValue());
    }

    private PagamentoRequest buildPagamentoRequest(String cardNumber, String paymentType) {
        var descricao = new DescricaoDTO();
        descricao.setValor("100.00");
        descricao.setDataHora("2024-01-01T10:00:00Z");
        descricao.setEstabelecimento("Loja Teste");

        var formaPagamento = new FormaPagamentoEmbeddable();
        formaPagamento.setTipo(paymentType);
        formaPagamento.setParcelas("1");

        var transacao = new TransacaoDTO();
        transacao.setCartao(cardNumber);
        transacao.setDescricao(descricao);
        transacao.setFormaPagamento(formaPagamento);

        var request = new PagamentoRequest();
        request.setTransacao(transacao);
        return request;
    }

    private PagamentoEntity buildPagamentoEntity(String status) {
        var descricao = new DescricaoEmbeddable();
        descricao.setValor("100.00");
        descricao.setDataHora("2024-01-01T10:00:00Z");
        descricao.setEstabelecimento("Loja Teste");
        descricao.setStatus(status);
        descricao.setNsu("1234567890");
        descricao.setCodigoAutorizacao("987654321");

        var formaPagamento = new FormaPagamentoEmbeddable();
        formaPagamento.setTipo(TipoPagamento.AVISTA.getValue());
        formaPagamento.setParcelas("1");

        var entity = new PagamentoEntity();
        entity.setId(1L);
        entity.setCartao("1234********3456");
        entity.setDescricao(descricao);
        entity.setFormaPagamento(formaPagamento);
        return entity;
    }
}
