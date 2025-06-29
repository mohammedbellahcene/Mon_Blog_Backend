package com.blog.api.config;

import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSecurityConfig {

    @Bean
    public OpenApiCustomizer securityOpenApiCustomizer() {
        return openApi -> {
            // Configuration OAuth2 pour la documentation
            SecurityScheme oauth2Scheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Authentification OAuth2 avec JWT")
                .flows(new OAuthFlows()
                    .password(new OAuthFlow()
                        .tokenUrl("/api/auth/login")
                        .refreshUrl("/api/auth/refresh")
                        .scopes(new Scopes()
                            .addString("read", "Accès en lecture")
                            .addString("write", "Accès en écriture")
                            .addString("admin", "Accès administrateur")
                        )
                    )
                );

            // Configuration Bearer JWT
            SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                    Authentification avec JWT Bearer Token.
                    
                    Pour obtenir un token :
                    1. Utilisez `/auth/login` avec vos identifiants
                    2. Copiez le token retourné
                    3. Cliquez sur le bouton 'Authorize' en haut
                    4. Entrez le token avec le préfixe 'Bearer '
                    
                    Exemple : Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                    """);

            // Ajout des schémas de sécurité à la documentation
            openApi.getComponents()
                .addSecuritySchemes("oauth2", oauth2Scheme)
                .addSecuritySchemes("bearer", bearerScheme);
        };
    }
} 