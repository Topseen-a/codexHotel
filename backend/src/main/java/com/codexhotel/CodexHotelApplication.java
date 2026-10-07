package com.codexhotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CodexHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexHotelApplication.class, args);
    }
}
