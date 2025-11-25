package com.api.pagamentos.dto;

import lombok.Data;

@Data
public class DescricaoDTO {
    private String valor;
    private String dataHora;
    private String estabelecimento;
    private String nsu;
    private String codigoAutorizacao;
    private String status;
}
