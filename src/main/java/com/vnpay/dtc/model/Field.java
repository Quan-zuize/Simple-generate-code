package com.vnpay.dtc.model;

import java.util.List;
import java.util.Map;

import com.vnpay.dtc.model.type.FormType;
import com.vnpay.dtc.model.type.JavaType;
import com.vnpay.dtc.model.type.MapType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Field {
    private int order;
    private String dbField;
    private String fieldName;
    private JavaType javaType;
    private boolean required;
    private String defaultValue;
    private boolean isId;
    private boolean toObjectType;
    private String mapObject;
    private MapType mapType;
    private FormType formType;
    private String formLabel;
    private List<String> additionKeys;
    private List<String> additionValues;
    private Map<String, List<Validator>> fieldValidators;
}
