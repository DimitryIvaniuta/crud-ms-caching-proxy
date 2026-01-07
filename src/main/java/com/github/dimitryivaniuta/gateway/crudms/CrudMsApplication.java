package com.github.dimitryivaniuta.gateway.crudms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.github.dimitryivaniuta.gateway.crudms")
public class CrudMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrudMsApplication.class, args);
    }
}
