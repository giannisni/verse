package com.getout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
@SpringBootApplication
@EnableScheduling

public class VerseApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerseApplication.class, args);
	}

}
