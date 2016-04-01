package cz.katona.pr.builder;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Bean
    public Module javaTimeModule(){
        return new JavaTimeModule();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
