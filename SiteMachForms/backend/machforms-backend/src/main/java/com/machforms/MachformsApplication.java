package com.machforms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MachformsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MachformsApplication.class, args);
    }
}
