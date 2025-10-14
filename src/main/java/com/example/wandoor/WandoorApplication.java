package com.example.wandoor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@SpringBootApplication
public class WandoorApplication {

	public static void main(String[] args) {
		SpringApplication.run(WandoorApplication.class, args);
        System.out.println("hello");
	}

}
