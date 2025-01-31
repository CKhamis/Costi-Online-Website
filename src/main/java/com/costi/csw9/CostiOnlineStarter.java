package com.costi.csw9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CostiOnlineStarter {
	public static void main(String[] args) {
		SpringApplication.run(CostiOnlineStarter.class, args);
	}
}
