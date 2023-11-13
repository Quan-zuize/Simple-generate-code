package com.vnpay.dtc.model.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ValidatorType {
    VAL_REQUIRED("required"),
    VAL_RANGE("range"),
    VAL_PATTERN("pattern");

    @Getter
    private final String value;

    ValidatorType(String value) {
        this.value = value;
    }
}
