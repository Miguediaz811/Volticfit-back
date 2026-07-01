package com.proyecto.volticfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class VolticfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(VolticfitApplication.class, args);
	}

}