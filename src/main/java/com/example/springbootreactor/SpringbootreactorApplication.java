package com.example.springbootreactor;

import com.example.springbootreactor.models.User;
import java.util.List;
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

    List<String> listNames = List.of("Andres Guzman", "Pedro Fulano", "Diego Sultano",
        "Maria Fulano", "Brucee Lee", "Bruce Willis");

    Flux<String> names = Flux.fromIterable(listNames);

    /*Flux<String> names = Flux.just("Andres Guzman", "Pedro Fulano", "Diego Sultano", "Maria Fulano",
        "Brucee Lee", "Bruce Willis");*/

    Flux<User> map = names.map(
            name -> new User(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
        .filter(user -> user.getName().equalsIgnoreCase("Bruce")).doOnNext(user -> {
          if (user == null) {
            throw new RuntimeException("Nombre no puede ser vacio");
          } else {
            System.out.println(user.getName().concat("" + user.getSurname()));
          }
        }).map(user -> {
          user.setName(user.getName().toLowerCase());
          return user;
        });

    names.subscribe(user -> log.info(user), error -> log.error(error.getMessage()),
        () -> log.info("Ha finalizado el flux."));
  }
}
