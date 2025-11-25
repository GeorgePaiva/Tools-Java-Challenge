package com.api.pagamentos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class DescricaoEmbeddable {
    @Column(name = "valor")
    private String valor;
    @Column(name = "data_hora")
    private String dataHora;
    @Column(name = "estabelecimento")
    private String estabelecimento;
    @Column(name = "nsu")
    private String nsu;
    @Column(name = "codigo_autorizacao")
    private String codigoAutorizacao;
    @Column(name = "status")
    private String status;

}
