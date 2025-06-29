package com.blog.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiInterceptorConfig {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Ajoute des headers de performance à toutes les réponses
            openApi.getPaths().values().forEach(path -> 
                path.readOperations().forEach(operation -> {
                    operation.getResponses().values().forEach(response -> {
                        response.addHeaderObject("X-Response-Time", createPerformanceHeader("Temps de réponse en millisecondes"));
                        response.addHeaderObject("X-Rate-Limit-Remaining", createPerformanceHeader("Nombre de requêtes restantes"));
                        response.addHeaderObject("X-Rate-Limit-Reset", createPerformanceHeader("Temps restant avant réinitialisation du quota"));
                    });
                    
                    // Ajoute des informations sur la pagination si applicable
                    if (operation.getParameters() != null && 
                        operation.getParameters().stream().anyMatch(p -> p.getName().equals("page"))) {
                        operation.getResponses().values().forEach(response -> {
                            response.addHeaderObject("X-Total-Count", createPerformanceHeader("Nombre total d'éléments"));
                            response.addHeaderObject("X-Total-Pages", createPerformanceHeader("Nombre total de pages"));
                            response.addHeaderObject("X-Page-Size", createPerformanceHeader("Taille de la page"));
                            response.addHeaderObject("X-Current-Page", createPerformanceHeader("Page courante"));
                        });
                    }
                })
            );

            // Ajoute des informations sur la mise en cache pour les GET
            openApi.getPaths().forEach((pathUrl, pathItem) -> {
                if (pathItem.getGet() != null) {
                    pathItem.getGet().getResponses().values().forEach(response -> {
                        response.addHeaderObject("Cache-Control", createPerformanceHeader("Directives de mise en cache"));
                        response.addHeaderObject("ETag", createPerformanceHeader("Tag de version de la ressource"));
                        response.addHeaderObject("Last-Modified", createPerformanceHeader("Dernière modification de la ressource"));
                    });
                }
            });

            // Ajoute des informations sur la compression
            openApi.getPaths().values().forEach(path ->
                path.readOperations().forEach(operation -> {
                    operation.getResponses().values().forEach(response -> {
                        response.addHeaderObject("Content-Encoding", createPerformanceHeader("Type de compression utilisé"));
                        response.addHeaderObject("Vary", createPerformanceHeader("Headers variables pour la mise en cache"));
                    });
                })
            );
        };
    }

    private Header createPerformanceHeader(String description) {
        return new Header()
            .description(description)
            .schema(new io.swagger.v3.oas.models.media.StringSchema());
    }
} 