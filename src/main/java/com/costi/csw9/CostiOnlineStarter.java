package com.costi.csw9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.costi.csw9.Repository")
public class CostiOnlineStarter {

	public static void main(String[] args) {
		SpringApplication.run(CostiOnlineStarter.class, args);
	}
}
