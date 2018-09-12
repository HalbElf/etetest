package org.db1.etetest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.db1.etetest.bom.repo")
public class EteTestServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EteTestServerApplication.class, args);
	}
}
