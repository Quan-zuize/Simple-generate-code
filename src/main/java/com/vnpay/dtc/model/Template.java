package com.vnpay.dtc.model;

import lombok.Data;

@Data
public class Template {
    private String name;
    private String fileName;
    private String fileExtension;
    private boolean isResource;
    private String subPath;
    private String templateFile;
}
