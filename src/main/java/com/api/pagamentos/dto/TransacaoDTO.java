package com.api.pagamentos.dto;

import com.api.pagamentos.entity.FormaPagamentoEmbeddable;
import lombok.Data;

@Data
public class TransacaoDTO {
    private Long id;
    private String cartao;
    private DescricaoDTO descricao;
    private FormaPagamentoEmbeddable formaPagamento;

}
