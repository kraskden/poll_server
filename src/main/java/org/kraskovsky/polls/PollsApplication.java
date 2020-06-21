package org.kraskovsky.polls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PollsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PollsApplication.class, args);
	}
}
