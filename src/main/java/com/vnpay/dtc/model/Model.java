package com.vnpay.dtc.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Model {
    private String tableName;
    private String objectName;
    private String comment;
    private String formName;
    private boolean generateDate;
    private List<Field> fields;
    private Map<String, String> customOptions;
}
