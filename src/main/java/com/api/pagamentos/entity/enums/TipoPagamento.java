package com.api.pagamentos.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPagamento {

    AVISTA("AVISTA"),
    PARCELADO_LOJA("PARCELADO LOJA"),
    PARCELADO_EMISSOR("PARCELADO EMISSOR");

    private final String value;

    TipoPagamento(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TipoPagamento fromValue(String value) {
        for (TipoPagamento t : TipoPagamento.values()) {
            if (t.value.equalsIgnoreCase(value.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de pagamento inválido: " + value);
    }
}
