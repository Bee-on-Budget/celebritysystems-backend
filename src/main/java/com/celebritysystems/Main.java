package com.celebritysystems;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Configuration
	public static class OpenApiConfig {

		// Swagger URL: http://localhost:8090/swagger-ui/index.html
		@Bean
		public OpenAPI customOpenAPI() {
			var securityKey = "Auth Token";
			var securityScheme = new SecurityScheme();
			securityScheme
					.name("bearerAuth")
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer");
			return new OpenAPI()
					.components(
							new Components().addSecuritySchemes(securityKey, securityScheme)
					)
					.addSecurityItem(new SecurityRequirement().addList(securityKey))
					.info(
							new Info()
									.title("Celebrity Systems - API")
									.version("1.0.0")
									.description("Backend documentation for Celebrity Systems app")
					);
		}
	}
}
