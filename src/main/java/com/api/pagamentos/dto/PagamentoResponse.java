package com.api.pagamentos.dto;

import lombok.Data;

@Data
public class PagamentoResponse {
    private TransacaoDTO transacao;
    private FormaPagamentoDTO formaPagamento;
}
