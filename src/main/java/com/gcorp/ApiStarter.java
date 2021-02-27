package com.gcorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.NoArgsConstructor;

@EnableJpaAuditing
@SpringBootApplication
@NoArgsConstructor
public class ApiStarter {
	public static void main(String[] args) {
		SpringApplication.run(ApiStarter.class, args);
	}
}
