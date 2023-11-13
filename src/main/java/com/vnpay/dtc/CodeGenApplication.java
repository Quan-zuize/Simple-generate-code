package com.vnpay.dtc;

import com.google.gson.Gson;
import com.vnpay.dtc.model.Project;
import com.vnpay.dtc.parser.ExcelModelParser;
import com.vnpay.dtc.service.VelocityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeGenApplication implements CommandLineRunner {

	@Autowired
	VelocityService velocityService;

	public static void main(String[] args) {
		SpringApplication.run(CodeGenApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String fileName = "config/CG_GamePortal.xlsx";
		Project project = new ExcelModelParser().parse(fileName);
		System.out.println("\"{\\\"request_id\\\":\\\"" + "123456" + "\\\"}\"");
//		System.out.println(new Gson().toJson(project));
		velocityService.process(project);
	}
}
