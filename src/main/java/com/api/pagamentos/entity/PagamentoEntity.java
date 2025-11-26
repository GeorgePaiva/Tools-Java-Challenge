package com.api.pagamentos.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "pagamentos")
public class PagamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "cartao")
    private String cartao;
    @Embedded
    private DescricaoEmbeddable descricao;
    @Embedded
    private FormaPagamentoEmbeddable formaPagamento;
    @Column(name = "created_at")
    private OffsetDateTime dataHora;
}
