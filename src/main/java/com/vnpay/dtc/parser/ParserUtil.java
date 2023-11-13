package com.vnpay.dtc.parser;

import com.vnpay.dtc.constant.AppConstant;
import com.vnpay.dtc.model.Project;
import com.vnpay.dtc.model.Template;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.Objects;

import static com.vnpay.dtc.constant.AppConstant.PACKAGE_PREFIX;

public class ParserUtil {
    VelocityContext context = new VelocityContext();
    VelocityEngine velocity = new VelocityEngine();

    private ParserUtil() {
    }

    public static String parseString(Cell cell) {
        if (Objects.isNull(cell)) {
            return "";
        }
        return cell.getStringCellValue().replace('\u00A0', ' ').trim();
    }

    public static int parseInt(Cell cell) {
        if (Objects.isNull(cell)) {
            return 0;
        }
        return Integer.parseInt(cell.getStringCellValue());
    }

    public static boolean parseBoolean(Cell cell) {
        if (Objects.isNull(cell)) {
            return false;
        }
        return Boolean.parseBoolean(cell.getStringCellValue());
    }

    public static <T extends Enum<T>> T parseEnum(Cell cell, T defaultValue) {
        if (Objects.isNull(cell)) {
            return defaultValue;
        }
        return (T) Enum.valueOf(defaultValue.getClass(),
                                cell.getStringCellValue().replace('\u00A0', ' ').toUpperCase().trim());
    }

    public static String getPackage(Project project, Template template) {
        return "package " + project.getPackageName() + "." + template.getSubPath() + ";\n";
    }

    public static String getImport(Project project, Template template, String model) {
        return "import " + project.getPackageName() + "." + template.getSubPath() + "." + model + ";\n";
    }

    public static String getObjectPackage(Project project, Template template) {
        return project.getPackageName() + "." + template.getSubPath();
    }

    public static String getPackageKey(String model) {
        return PACKAGE_PREFIX + "." + model;
    }

    public static String evaluate(VelocityEngine velocity, VelocityContext velocityContext, String content) {
        StringWriter writer = new StringWriter();
        velocity.evaluate(velocityContext, writer, "", content);
        return writer.toString();
    }

    public static String packageToFolder(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().replace(AppConstant.PERIOD, AppConstant.SLASH);
    }

    public static String folderToPackage(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().replace(AppConstant.SLASH, AppConstant.PERIOD).replace("\\", AppConstant.PERIOD);
    }

    public static String removeHump(String text) {
        return removeHump(text, "_");
    }

    public static String removeHump(String text, String replace) {
        if (!StringUtils.hasText(text)) {
            return text;
        }
        StringBuilder builder = new StringBuilder();

        char firstChar = text.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            builder.append(Character.toLowerCase(firstChar));
        }else {
            builder.append(firstChar);
        }

        for (int i = 1; i < text.length(); i++) {
            char chr = text.charAt(i);
            builder.append(Character.isUpperCase(chr) ? replace + Character.toLowerCase(chr) : chr);
        }

        return builder.toString();
    }
}
