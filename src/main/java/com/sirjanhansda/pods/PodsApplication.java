package com.sirjanhansda.pods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.sirjanhansda.pods")
public class PodsApplication {
	public static void main(String[] args) {
		SpringApplication.run(PodsApplication.class, args);
	}

}
