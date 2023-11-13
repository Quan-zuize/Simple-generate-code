package com.vnpay.dtc.service;

import com.vnpay.dtc.constant.AppConstant;
import com.vnpay.dtc.model.Model;
import com.vnpay.dtc.model.Project;
import com.vnpay.dtc.model.Template;
import com.vnpay.dtc.model.TemplateGroup;
import com.vnpay.dtc.parser.ParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class VelocityService {

    List<String> prefixes = Arrays.asList("SA");
    VelocityContext context;
    VelocityEngine velocity;

    @PostConstruct
    void init() {
        velocity = new VelocityEngine();
        velocity.loadDirective("com.vnpay.dtc.directive.GetPackage");
        velocity.loadDirective("com.vnpay.dtc.directive.ImportObject");
        velocity.loadDirective("com.vnpay.dtc.directive.LowerFirst");
        velocity.loadDirective("com.vnpay.dtc.directive.UpperFirst");
        velocity.loadDirective("com.vnpay.dtc.directive.ToFolder");
        velocity.loadDirective("com.vnpay.dtc.directive.ToPackage");
        velocity.setProperty(Velocity.RESOURCE_LOADERS, "string");


        context = new VelocityContext();
        Calendar calendar = Calendar.getInstance();
        context.internalPut("YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
        context.internalPut("MONTH", String.valueOf(calendar.get(Calendar.MONTH) + 1));
        context.internalPut("DAY", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        context.internalPut("DATE", DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd"));
        context.internalPut("TIME", DateFormatUtils.format(calendar.getTime(), "HH:mm:ss"));
        context.internalPut("NOW", DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
        context.internalPut("USER", System.getProperty("user.name"));
        context.internalPut("hash", "#");
        context.internalPut("usd", "$");
        context.internalPut("eq", "==");
        context.internalPut("neq", "!=");
    }

    public void process(Project project) {
        for (String key : project.getCustomOptions().keySet()) {
            context.internalPut(key, project.getCustomOptions().get(key));
        }

        for (Model model : project.getModels().values()) {
            log.info("Setting context object from model: {}", model.getObjectName());
            setModelContext(model);
            for (TemplateGroup group : project.getTemplateGroups().values()) {
                log.info("Setting context object from group: {}", group.getName());
                for (Template template : group.getTemplates()) {
                    log.info("Template: {}", template.getName());
                    setTemplateContext(project, model, template);
                }
            }
            log.info("Final context variables:");
            for (String key : context.internalGetKeys()) {
                log.info("{} : {}", key, context.internalGet(key));
            }

            for (TemplateGroup group : project.getTemplateGroups().values()) {
                log.info("=====================");
                log.info("Processing group: {}", group.getName());
                for (Template template : group.getTemplates()) {
                    log.info("======== template: {}", template.getName());
                    try {
                        String parsedContent = processTemplate(project, model, template);

                        String fileName = ParserUtil.evaluate(velocity, context, template.getFileName());
                        StringBuilder builder = new StringBuilder();
                        builder.append(project.getOutputDirectory()).append(AppConstant.PERIOD);
                        if (template.isResource()) {
                            builder.append(project.getResourcePath()).append(
                                    AppConstant.PERIOD);
                        } else {
                            builder.append(project.getJavaPath()).append(
                                    AppConstant.PERIOD);
                            builder.append(project.getPackageName()).append(AppConstant.PERIOD);
                        }
                        builder.append(ParserUtil.evaluate(velocity, context, template.getSubPath())).append(
                                AppConstant.PERIOD);
                        String folderPath = ParserUtil.packageToFolder(builder.toString());
                        Files.createDirectories(Paths.get(folderPath));
                        String filePath = folderPath + fileName + AppConstant.PERIOD + template.getFileExtension();
                        log.info("Storing content into file: {}", filePath);
                        Files.write(Paths.get(filePath), parsedContent.getBytes(), StandardOpenOption.CREATE);
                        log.info("Processed template: {}.", template.getName());

                    } catch (IOException exception) {
                        log.error("Template " + template.getName() + " Error", exception);
                    }
                }
            }

        }

    }

    private void setModelContext(Model model) {
        context.internalPut("fields", model.getFields());
        context.internalPut("table", model.getTableName());
        context.internalPut("model", model.getObjectName());
        context.internalPut("comment", model.getComment());
        context.internalPut("formName", model.getFormName());
        String name = model.getObjectName();
        context.internalPut("name", name);
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) {
                context.internalPut("name", name.substring(prefix.length()));
            }
        }
        for (String key : model.getCustomOptions().keySet()) {
            context.internalPut(key, model.getCustomOptions().get(key));
        }
        context.internalPut("nameLower", StringUtils.uncapitalize(name));
        context.internalPut("name_", ParserUtil.removeHump(name));
        context.internalPut("nameUpper", name.toUpperCase());
        context.internalPut("generateDate", model.isGenerateDate());
    }

    private void setTemplateContext(Project project, Model model, Template template) {
        log.info("Setting package context: {}, Template: {}, File: {}", model.getObjectName(),
                 template.getName(), template.getTemplateFile());

        String modelName = ParserUtil.evaluate(velocity, context, template.getFileName());
        String objectPackage = ParserUtil.getObjectPackage(project, template);

        context.internalPut(ParserUtil.getPackageKey(modelName), objectPackage + "." + modelName);
    }

    private String processTemplate(Project project, Model model, Template template) throws IOException {
        log.info("Processing Project: {}, Model: {}, Template: {}, File: {}", project.getName(), model.getObjectName(),
                 template.getName(), template.getTemplateFile());
        String content = new String(Files.readAllBytes(Paths.get(template.getTemplateFile())));
        StringBuilder builder = new StringBuilder();
        if (template.getFileExtension().equals("java")) {
            builder.append(ParserUtil.getPackage(project, template));
            builder.append("\n");
        }
        builder.append(content);
        String parsedContent = ParserUtil.evaluate(velocity, context, builder.toString());

        return parsedContent;
    }
}
