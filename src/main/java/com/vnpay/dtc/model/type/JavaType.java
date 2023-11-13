package com.vnpay.dtc.model.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum JavaType {
    BYTE("byte"),
    SHORT("short"),
    INT("int"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    CHAR("char"),
    STRING("String"),
    BOOLEAN("boolean"),
    OBJECT("Object"),
    DATE("Date");

    @Getter
    private final String value;

    JavaType(String value) {
        this.value = value;
    }
}
