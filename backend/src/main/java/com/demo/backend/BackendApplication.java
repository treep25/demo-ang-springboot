package com.demo.backend;

import com.demo.backend.model.Tutorial;
import com.demo.backend.repository.TutorialRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(TutorialRepository tutorialRepository) {
		return args -> {
			tutorialRepository.save(Tutorial.builder().id(1L).description("DESC").title("New Tutorial").build());
		};
	}
}
