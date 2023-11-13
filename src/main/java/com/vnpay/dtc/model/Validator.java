package com.vnpay.dtc.model;

import com.vnpay.dtc.model.type.ValidatorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Validator {
    private ValidatorType validator;
    private String min;
    private String max;
    private String pattern;
    private String message;
}
