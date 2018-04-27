package xyz.geekweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * @author lhao
 */
@SpringBootApplication
@EnableScheduling
public class GeekWebApplication extends SpringBootServletInitializer {


    public static void main(String[] args) throws IOException {

        SpringApplication.run(GeekWebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GeekWebApplication.class);
    }
}
