package com.edusync.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EduSyncBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduSyncBackendApplication.class, args);
	} 

}
