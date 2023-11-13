package com.vnpay.dtc.parser;

import com.vnpay.dtc.model.*;
import com.vnpay.dtc.model.type.FormType;
import com.vnpay.dtc.model.type.JavaType;
import com.vnpay.dtc.model.type.MapType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Get project information from excel file parser
 */
@Slf4j
public class ExcelModelParser implements ModelParser {
    public static final String PROJECT_SHEET = "00";
    public static final String TEMPLATE_PREFIX = "TG";
    public static final String MODEL_PREFIX = "M";

    @Override
    public Project parse(String content) {
        Project project = new Project();
        try {
            FileInputStream file = new FileInputStream(content);
            try (Workbook workbook = new XSSFWorkbook(file)) {
                setProjectInfo(workbook, project);
                setProjectTemplate(workbook, project);
                setProjectModel(workbook, project);
            }
        } catch (IOException ioException) {

        }
        return project;
    }

    private void setProjectInfo(Workbook workbook, Project project) {
        Sheet projectSheet = workbook.getSheet(PROJECT_SHEET);
        for (int row = 0; row < 7; row++) {
            if (Objects.nonNull(projectSheet.getRow(row).getCell(1))) {
                projectSheet.getRow(row).getCell(1).setCellType(CellType.STRING);
            }
        }

        project.setName(ParserUtil.parseString(projectSheet.getRow(0).getCell(1)));
        project.setJavaPath(ParserUtil.parseString(projectSheet.getRow(1).getCell(1)));
        project.setResourcePath(ParserUtil.parseString(projectSheet.getRow(2).getCell(1)));
        project.setPackageName(ParserUtil.parseString(projectSheet.getRow(3).getCell(1)));
        project.setTemplateFolder(ParserUtil.parseString(projectSheet.getRow(4).getCell(1)));
        project.setOutputDirectory(ParserUtil.parseString(projectSheet.getRow(5).getCell(1)));
        project.setOverrideFile(ParserUtil.parseBoolean(projectSheet.getRow(6).getCell(1)));

        project.setCustomOptions(getCustomOption(projectSheet, 0, 3));
    }

    private void setProjectTemplate(Workbook workbook, Project project) {
        Map<String, TemplateGroup> templateGroups = new HashMap<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (workbook.getSheetAt(i).getSheetName().toUpperCase().startsWith(TEMPLATE_PREFIX)) {
                Sheet sheet = workbook.getSheetAt(i);
                String groupName = sheet.getRow(0).getCell(1).getStringCellValue().trim().toUpperCase();
                TemplateGroup templateGroup = new TemplateGroup();
                templateGroup.setName(groupName);

                int rowNum = 4;
                Row currentRow = sheet.getRow(rowNum);
                List<Template> templates = new ArrayList<>();
                while (Objects.nonNull(currentRow) && Objects.nonNull(currentRow.getCell(0))) {
                    for (int column = 0; column < 6; column++) {
                        if (Objects.nonNull(currentRow.getCell(column))) {
                            currentRow.getCell(column).setCellType(CellType.STRING);
                        }
                    }
                    Template template = new Template();
                    template.setName(ParserUtil.parseString(currentRow.getCell(0)));
                    template.setFileName(ParserUtil.parseString(currentRow.getCell(1)));
                    template.setFileExtension(ParserUtil.parseString(currentRow.getCell(2)));
                    template.setResource(ParserUtil.parseBoolean(currentRow.getCell(3)));
                    template.setSubPath(ParserUtil.parseString(currentRow.getCell(4)));
                    template.setTemplateFile(ParserUtil.parseString(currentRow.getCell(5)));
                    templates.add(template);
                    currentRow = sheet.getRow(++rowNum);
                }
                templateGroup.setTemplates(templates);
                templateGroups.put(templateGroup.getName(), templateGroup);
            }
        }
        project.setTemplateGroups(templateGroups);
    }

    private void setProjectModel(Workbook workbook, Project project) {
        Map<String, Model> modelMap = new HashMap<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (workbook.getSheetAt(i).getSheetName().toUpperCase().startsWith(MODEL_PREFIX)) {
                Sheet sheet = workbook.getSheetAt(i);
                boolean skipGenerate = sheet.getRow(5).getCell(1).getBooleanCellValue();
                if (skipGenerate) {
                    continue;
                }
                Model model = new Model();
                model.setTableName(sheet.getRow(0).getCell(1).getStringCellValue().trim());
                model.setObjectName(sheet.getRow(1).getCell(1).getStringCellValue().trim());
                model.setComment(sheet.getRow(2).getCell(1).getStringCellValue().trim());
                model.setFormName(sheet.getRow(3).getCell(1).getStringCellValue().trim());
                model.setGenerateDate(sheet.getRow(4).getCell(1).getBooleanCellValue());

                model.setCustomOptions(getCustomOption(sheet, 0, 3));

                List<Field> fields = new ArrayList<>();
                int rowNum = 10;
                Row currentRow = sheet.getRow(rowNum);
                while (Objects.nonNull(currentRow) && Objects.nonNull(currentRow.getCell(1))) {
                    Field field = new Field();
                    for (int column = 0; column < 13; column++) {
                        if (Objects.nonNull(currentRow.getCell(column))) {
                            currentRow.getCell(column).setCellType(CellType.STRING);
                        }
                    }
                    field.setOrder(Integer.parseInt(currentRow.getCell(0).getStringCellValue()));
                    field.setDbField(currentRow.getCell(1).getStringCellValue());
                    field.setFieldName(currentRow.getCell(2).getStringCellValue());
                    field.setJavaType(ParserUtil.parseEnum(currentRow.getCell(3), JavaType.STRING));
                    field.setRequired(ParserUtil.parseBoolean(currentRow.getCell(4)));
                    field.setDefaultValue(ParserUtil.parseString(currentRow.getCell(5)));
                    field.setId(ParserUtil.parseBoolean(currentRow.getCell(6)));
                    field.setToObjectType(ParserUtil.parseBoolean(currentRow.getCell(7)));
                    field.setMapObject(ParserUtil.parseString(currentRow.getCell(8)));
                    field.setMapType(ParserUtil.parseEnum(currentRow.getCell(9), MapType.SINGLE));
                    field.setFormType(ParserUtil.parseEnum(currentRow.getCell(10), FormType.TEXT));
                    field.setFormLabel(ParserUtil.parseString(currentRow.getCell(11)));
                    field.setAdditionKeys(Arrays.asList(ParserUtil.parseString(currentRow.getCell(12)).split(",")));
                    field.setAdditionValues(Arrays.asList(ParserUtil.parseString(currentRow.getCell(13)).split(",")));
                    fields.add(field);
                    currentRow = sheet.getRow(++rowNum);
                }
                model.setFields(fields);

                modelMap.put(model.getObjectName(), model);
            }
        }
        project.setModels(modelMap);
    }

    private Map<String, String> getCustomOption(Sheet sheet, int row, int column) {
        Map<String, String> customOptions = new HashMap<>();
        Row keyRow = sheet.getRow(row);
        Row valueRow = sheet.getRow(row + 1);
        while (Objects.nonNull(keyRow.getCell(column))) {
            String value = valueRow.getCell(column).getStringCellValue();
            customOptions.put(keyRow.getCell(column).getStringCellValue(), value);
            column++;
        }
        return customOptions;
    }
}
