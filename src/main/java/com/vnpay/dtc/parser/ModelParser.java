package com.vnpay.dtc.parser;

import com.vnpay.dtc.model.Project;

public interface ModelParser {
    public Project parse(String content);
}
