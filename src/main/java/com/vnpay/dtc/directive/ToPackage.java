package com.vnpay.dtc.directive;

import com.vnpay.dtc.parser.ParserUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;

public class ToPackage extends Directive {
    @Override
    public String getName() {
        return "toPackage";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter contextAdapter, Writer writer, Node node) throws IOException,
            ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String value = ((String) node.jjtGetChild(0).value(contextAdapter)).trim();
        writer.write(ParserUtil.folderToPackage(value));
        return true;
    }
}
