package com.rocket.rocket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rocket.rocket.mapper")
public class RocketlibApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RocketlibApiApplication.class, args);
	}

}
