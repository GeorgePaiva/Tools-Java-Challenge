package com.api.pagamentos.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagamentoRequest {
    private BigDecimal valor;
    private String cartao;
    private FormaPagamentoDTO formaPagamento;
}
