package com.sr.dataexport.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName OpenAPISecurityConfiguration
 * @Description This class is used to configure the security for the OpenAPI.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Batch Data Generator",
                version = "1.0",
                description = "Generate data for testing purposes"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenAPISecurityConfiguration {
}
