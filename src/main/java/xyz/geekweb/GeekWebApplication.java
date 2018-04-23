package xyz.geekweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GeekWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeekWebApplication.class, args);
    }
}
