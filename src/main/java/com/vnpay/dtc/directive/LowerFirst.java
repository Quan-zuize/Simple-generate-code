package com.vnpay.dtc.directive;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;

public class LowerFirst extends Directive {
    @Override
    public String getName() {
        return "lowerFirst";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter contextAdapter, Writer writer, Node node) throws IOException,
            ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String value = (String) node.jjtGetChild(0).value(contextAdapter);
        if (!StringUtils.hasText(value)) {
            writer.write("");
            return true;
        }
        writer.write(StringUtils.uncapitalize(value.trim()));
        return true;
    }
}
