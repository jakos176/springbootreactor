package com.example.springbootreactor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootreactorApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(SpringbootreactorApplication.class, args);
  }

	@Override
	public void run(String... args) {
		Flux<String> names = Flux.just("Andres", "Pedro", "Diego")
				.doOnNext(System.out::println);

		names.subscribe();

	}
}
