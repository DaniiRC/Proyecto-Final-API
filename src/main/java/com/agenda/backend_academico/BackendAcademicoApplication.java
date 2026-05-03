package com.agenda.backend_academico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendAcademicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendAcademicoApplication.class, args);
	} 

}
