package com.sr.dataexport;

import com.sr.dataexport.security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class SpringbatchDataexportApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbatchDataexportApplication.class, args);
    }

}
