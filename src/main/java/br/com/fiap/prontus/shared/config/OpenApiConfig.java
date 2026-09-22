package br.com.fiap.prontus.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI prontusOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prontus API")
                        .description("MVP de Triagem Inteligente e Fila Dinâmica em Tempo Real para o SUS — FIAP MBA Arquitetura e Desenvolvimento Java")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Documento Mestre de Engenharia — Prontus")));
    }
}
