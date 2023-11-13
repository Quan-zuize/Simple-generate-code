package com.vnpay.dtc.model.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum MapType {
    SINGLE("single"),
    LIST("list"),
    MAP("map"),
    ARRAY("array");

    @Getter
    private final String value;

    MapType(String value) {
        this.value = value;
    }
}
