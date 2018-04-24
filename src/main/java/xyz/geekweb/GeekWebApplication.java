package xyz.geekweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * @author lhao
 */
@SpringBootApplication
@EnableScheduling
public class GeekWebApplication {


    public static void main(String[] args) throws IOException {

        SpringApplication.run(GeekWebApplication.class, args);
    }
}
