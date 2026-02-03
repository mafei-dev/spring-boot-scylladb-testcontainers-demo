package com.example.springbootscylladbtestcontainersdemo;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootScylladbTestcontainersDemoApplication {

    private final CqlSession cqlSession;

    public SpringBootScylladbTestcontainersDemoApplication(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootScylladbTestcontainersDemoApplication.class, args);
    }

}
