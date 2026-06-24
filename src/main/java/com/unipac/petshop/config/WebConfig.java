package com.unipac.petshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configuração para permitir o acesso web a diretórios físicos do sistema
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Libera a leitura da pasta local "uploads" para que o navegador consiga exibir as fotos salvas
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}