package com.example.springbootreactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootreactorApplication implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(SpringbootreactorApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(SpringbootreactorApplication.class, args);
  }

  @Override
  public void run(String... args) {
    Flux<String> names = Flux.just("Andres", "Pedro", "Diego", "")
        .doOnNext(name -> {
              if (name.isEmpty()) {
                throw new RuntimeException("Nombre no puede ser vacio");
              } else {
                System.out.println(name);
              }
            }
        );

    names.subscribe(log::info, error -> log.error(error.getMessage()));
  }
}
