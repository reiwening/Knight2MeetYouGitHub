package com.g5.cs203proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.g5.cs203proj.entity"})
public class Cs203projApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cs203projApplication.class, args);
	}

}
