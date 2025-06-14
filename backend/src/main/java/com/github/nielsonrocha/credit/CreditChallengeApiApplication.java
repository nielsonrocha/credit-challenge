package com.github.nielsonrocha.credit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CreditChallengeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditChallengeApiApplication.class, args);
	}

}
