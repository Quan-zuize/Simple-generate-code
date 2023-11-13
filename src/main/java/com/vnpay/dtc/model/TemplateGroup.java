package com.vnpay.dtc.model;

import lombok.Data;

import java.util.List;

@Data
public class TemplateGroup {
    private String name;
    List<Template> templates;
}
