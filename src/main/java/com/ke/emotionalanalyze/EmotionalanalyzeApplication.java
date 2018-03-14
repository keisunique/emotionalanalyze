package com.ke.emotionalanalyze;

import com.ke.emotionalanalyze.service.CommentsSevice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmotionalanalyzeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmotionalanalyzeApplication.class, args);
	}
}
