package com.systemcraftsman.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings({"HideUtilityClassConstructor"})
public class TodoServiceApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TodoServiceApplication.class, args);
    }
}
