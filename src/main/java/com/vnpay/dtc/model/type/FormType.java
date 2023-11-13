package com.vnpay.dtc.model.type;

import lombok.Getter;

@Getter
public enum FormType {
    TEXT("text"),
    DROPDOWN("dropdown"),
    CHECKBOX("checkbox"),
    RADIO("radio"),
    DATE("date"),
    DATETIME("datetime"),
    DATERANGE("daterange");

    @Getter
    private final String value;

    FormType(String value) {
        this.value = value;
    }
}
