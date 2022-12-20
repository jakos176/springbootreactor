package com.example.springbootreactor;

import com.example.springbootreactor.models.Comments;
import com.example.springbootreactor.models.User;
import com.example.springbootreactor.models.UserComments;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringbootreactorApplication implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(SpringbootreactorApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(SpringbootreactorApplication.class, args);
  }

  @Override
  public void run(String... args) {
    ejemploUserComenntFlatMap();
  }

  public void ejemploFlatMap() {
    List<String> listNames = List.of("Andres Guzman", "Pedro Fulano", "Diego Sultano",
        "Maria Fulano", "Brucee Lee", "Bruce Willis");

    Flux.fromIterable(listNames).
        map(name -> new User(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
        .flatMap(user -> {
          if (user.getName().equalsIgnoreCase("bruce")) {
            return Mono.just(user);
          } else {
            return Mono.empty();
          }
        })
        .filter(user -> user.getName().equalsIgnoreCase("Bruce"))
        .map(user -> {
          user.setName(user.getName().toLowerCase());
          return user;
        }).subscribe(user -> log.info(user.toString()));
  }

  public void ejemploCollectList() {
    List<User> users = new ArrayList<>();
    users.add(new User("Andres ", "Guzman"));
    users.add(new User("Pedro", "Fulano"));
    users.add(new User("Diego", "Sultano"));
    users.add(new User("Maria", "Fulano"));
    users.add(new User("Brucee", "Lee"));
    users.add(new User("Brucee", "Willis"));

    Flux.fromIterable(users)
        .collectList()
        .subscribe(lista -> lista.forEach(user -> log.info(user.toString())));
  }

  public void ejemploToString() {
    List<User> users = new ArrayList<>();
    users.add(new User("Andres ", "Guzman"));
    users.add(new User("Pedro", "Fulano"));
    users.add(new User("Diego", "Sultano"));
    users.add(new User("Maria", "Fulano"));
    users.add(new User("Brucee", "Lee"));
    users.add(new User("Brucee", "Willis"));

    Flux.fromIterable(users).
        map(user -> user.getName().toUpperCase().concat(" ")
            .concat(user.getSurname().toUpperCase()))
        .flatMap(name -> {
          if (name.contains("Brucee".toUpperCase())) {
            return Mono.just(name);
          } else {
            return Mono.empty();
          }
        }).map(String::toLowerCase).subscribe(log::info);
  }

  public void ejemploIterable() {
    List<String> listNames = List.of("Andres Guzman", "Pedro Fulano", "Diego Sultano",
        "Maria Fulano", "Brucee Lee", "Bruce Willis");

    Flux<String> names = Flux.fromIterable(listNames);

    names.map(
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

  private void ejemploUserComenntFlatMap() {
    Mono<User> userMono = Mono.fromCallable(() -> new User("John", "Doe"));
    Mono<Comments> commentMono = Mono.fromCallable(() -> {
      Comments comments = new Comments();
      comments.AddComment("hello!");
      comments.AddComment("goodbye!");
      return comments;
    });

    userMono.flatMap(user -> commentMono.map(comment -> new UserComments(user, comment)))
        .subscribe(userComments -> log.info(userComments.toString()));
  }

}
