package com.proyecto.volticfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VolticfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(VolticfitApplication.class, args);
	}

}
