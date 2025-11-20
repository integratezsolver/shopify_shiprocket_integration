package com.integratez.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties
@SpringBootApplication
public class IntegratezPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegratezPlatformApplication.class, args);
	}

}
