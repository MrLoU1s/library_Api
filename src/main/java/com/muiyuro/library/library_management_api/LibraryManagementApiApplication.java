package com.muiyuro.library.library_management_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
public class LibraryManagementApiApplication {

	public static void main(String[] args) {

        SpringApplication.run(LibraryManagementApiApplication.class, args);
	}

}
