package com.example.springbootscylladbtestcontainersdemo;

import org.springframework.boot.SpringApplication;

public class TestSpringBootScylladbTestcontainersDemoApplication {


    public static void main(String[] args) {
        SpringApplication.from(SpringBootScylladbTestcontainersDemoApplication::main)
                .with(TestcontainersConfiguration.class).run(args);
    }

}
