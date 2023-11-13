package com.vnpay.dtc.directive;

import com.vnpay.dtc.parser.ParserUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

public class ImportObject extends Directive {
    @Override
    public String getName() {
        return "importObject";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter contextAdapter, Writer writer, Node node) throws IOException,
            ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String clazz = ((String) node.jjtGetChild(0).value(contextAdapter)).trim();
        if (contextAdapter.containsKey(ParserUtil.getPackageKey(clazz))) {
            String packagePath = contextAdapter.get(ParserUtil.getPackageKey(clazz)).toString();
            packagePath = new StringBuilder("import ").append(packagePath).append(";\n").toString();
            writer.write(packagePath);
            return true;
        }
        return false;
    }
}
