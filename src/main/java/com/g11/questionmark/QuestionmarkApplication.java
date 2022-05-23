package com.g11.questionmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class QuestionmarkApplication extends SpringBootServletInitializer {

	    public static void main(String[] args) {
		        SpringApplication.run(QuestionmarkApplication.class, args);
	    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(QuestionmarkApplication.class);
    }

}