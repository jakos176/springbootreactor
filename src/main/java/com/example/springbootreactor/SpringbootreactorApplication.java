package com.example.springbootreactor;

import com.example.springbootreactor.models.Comments;
import com.example.springbootreactor.models.User;
import com.example.springbootreactor.models.UserComments;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
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
  public void run(String... args) throws InterruptedException {
    ejemploContrapresion();
  }

  public void ejemploInterval() {
    Flux<Integer> range = Flux.range(1, 12);
    Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
    range.zipWith(interval, (r, i) -> i).subscribe(i -> log.info(i.toString()));
    //usar block last para ver el resultado en pantalla
  }

  public void ejemploIntervalElement() {
    Flux.range(1, 12).delayElements(Duration.ofSeconds(1))
        .subscribe(i -> log.info(i.toString()));
    //usar block last para ver el resultado en pantalla
  }

  public void ejemploContrapresion() {
    Flux.range(1, 10)
        .log()
        .limitRate(5)
        .subscribe();/*new Subscriber<>() {


          private Subscription subscription;
          private int limit = 5;
          private int consumed = 0;

          @Override
          public void onSubscribe(Subscription s) {
            this.subscription = s;
            subscription.request(limit);
          }

          @Override
          public void onNext(Integer integer) {
            log.info(integer.toString());
            consumed++;
            if (consumed == limit) {
              consumed = 0;
              subscription.request(limit);
            }
          }

          @Override
          public void onError(Throwable t) {

          }

          @Override
          public void onComplete() {

          }
        }*/
  }

  public void ejemploIntervalInfiniteDesdeCreate() {
    Flux.create(emitter -> {
          Timer timer = new Timer();
          timer.schedule(new TimerTask() {

            private int contador = 0;

            @Override
            public void run() {
              emitter.next(++contador);
              if (contador == 10) {
                timer.cancel();
                emitter.complete();
              }
              if (contador == 5) {
                emitter.error(new NullPointerException("Has metido una null"));
                timer.cancel();
              }

            }
          }, 1000, 1000);
        })
        .subscribe(next -> log.info(next.toString()),
            next -> log.error(next.getMessage()),
            () -> log.info("Hemos terminado"));
  }

  public void ejemploIntervalInfinite() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Flux.interval(Duration.ofSeconds(1))
        .doOnTerminate(latch::countDown)
        .flatMap(i -> {
          if (i >= 5) {
            return Flux.error(new InterruptedException("Solo hasta 5"));
          }
          return Flux.just(i);
        })
        .map(i -> "Hola " + 1)
        .retry(2)
        .doOnNext(s -> log.info(s.toString())).subscribe();
    latch.await();
  }

  private void ejemploWithZipRanges() {
    Flux<Integer> range = Flux.range(0, 4);
    Flux.just(1, 2, 3, 4).map(integer -> integer * 2)
        .zipWith(range, (uno, dos) -> String.format("Primer flux: %d, Segundo Flux: %d", uno, dos)
        ).subscribe(log::info);
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

  private void ejemploUserComenntZipWith2() {
    Mono<User> userMono = Mono.fromCallable(() -> new User("John", "Doe"));
    Mono<Comments> commentMono = Mono.fromCallable(() -> {
      Comments comments = new Comments();
      comments.AddComment("hello!");
      comments.AddComment("goodbye!");
      return comments;
    });

    Mono<UserComments> userCommentsMono = userMono.zipWith(commentMono)
        .map(tuple -> {
          User user = tuple.getT1();
          Comments comments = tuple.getT2();
          return new UserComments(user, comments);
        });
    userCommentsMono.subscribe(userComments -> log.info(userComments.toString()));
  }

  private void ejemploUserComenntZipWith() {
    Mono<User> userMono = Mono.fromCallable(() -> new User("John", "Doe"));
    Mono<Comments> commentMono = Mono.fromCallable(() -> {
      Comments comments = new Comments();
      comments.AddComment("hello!");
      comments.AddComment("goodbye!");
      return comments;
    });

    Mono<UserComments> userCommentsMono = userMono.zipWith(commentMono, UserComments::new);
    userCommentsMono.subscribe(userComments -> log.info(userComments.toString()));
  }

}
