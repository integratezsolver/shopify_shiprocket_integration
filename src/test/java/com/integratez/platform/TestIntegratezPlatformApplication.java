package com.integratez.platform;

import org.springframework.boot.SpringApplication;

public class TestIntegratezPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(IntegratezPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
