package com.api.pagamentos.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusTransacao {

    AUTORIZADO("AUTORIZADO"),
    NEGADO("NEGADO"),
    CANCELADO("CANCELADO");

    private final String value;

    StatusTransacao(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusTransacao fromValue(String value) {
        for (StatusTransacao s : StatusTransacao.values()) {
            if (s.value.equalsIgnoreCase(value.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }
}
