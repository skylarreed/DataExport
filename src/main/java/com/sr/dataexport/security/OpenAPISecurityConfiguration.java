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
                title = "Batch Data Export",
                version = "1.0",
                description = "Export data based on transaction properties."
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
