package com.vnpay.dtc.model;

import lombok.Data;

import java.util.Map;

@Data
public class Project {
    private String name;
    private String javaPath;
    private String resourcePath;
    private String packageName;
    private String templateFolder;
    private String outputDirectory;
    private boolean overrideFile;
    private Map<String, String> customOptions;
    private Map<String, TemplateGroup> templateGroups;
    private Map<String, Model> models;
}
