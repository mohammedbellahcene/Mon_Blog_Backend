package com.blog.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Blog")
                .description("""
                    API complète pour la gestion d'un blog moderne avec authentification, articles, commentaires et réactions.
                    
                    ## Fonctionnalités principales
                    
                    - 👤 Authentification JWT avec refresh tokens
                    - 📝 Gestion des articles avec support Markdown
                    - 💬 Système de commentaires
                    - 👍 Système de réactions (likes/dislikes)
                    - 🎨 Gestion des thèmes
                    - 📎 Upload de fichiers
                    - 🔔 Système de notifications
                    
                    ## Guide d'utilisation rapide
                    
                    1. Créez un compte via `/auth/register`
                    2. Connectez-vous via `/auth/login`
                    3. Utilisez le token JWT retourné pour les autres requêtes
                    
                    ## Bonnes pratiques
                    
                    - Utilisez la pagination pour les listes
                    - Respectez les limites de rate limiting
                    - Vérifiez les codes HTTP retournés
                    - Utilisez les endpoints de validation
                    """)
                .version("2.0.0")
                .contact(new Contact()
                    .name("Support Blog")
                    .email("support@blog.com")
                    .url("https://blog.com/support"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .externalDocs(new ExternalDocumentation()
                .description("Documentation complète")
                .url("https://blog.com/docs"))
            .servers(Arrays.asList(
                new Server()
                    .url("http://localhost:8080")
                    .description("Serveur de développement"),
                new Server()
                    .url("https://api.blog.com")
                    .description("Serveur de production")))
            .tags(Arrays.asList(
                createTag("auth", "Authentification et gestion des utilisateurs", """
                    Endpoints pour l'inscription, la connexion et la gestion du profil utilisateur.
                    Inclut la gestion des tokens JWT et le refresh token."""),
                createTag("posts", "Gestion des articles", """
                    CRUD complet pour les articles du blog.
                    Support du Markdown, des tags et des catégories."""),
                createTag("comments", "Gestion des commentaires", """
                    Système de commentaires avec support des réponses imbriquées
                    et de la modération."""),
                createTag("reactions", "Système de réactions", """
                    Gestion des likes et dislikes sur les articles et commentaires.
                    Support des métriques d'engagement."""),
                createTag("themes", "Gestion des thèmes", """
                    Personnalisation de l'apparence du blog avec support
                    des thèmes personnalisés."""),
                createTag("files", "Gestion des fichiers", """
                    Upload et gestion des fichiers média.
                    Support des images, vidéos et documents."""),
                createTag("notifications", "Système de notifications", """
                    Notifications en temps réel pour les interactions
                    et les nouveaux contenus.""")))
            .components(new Components());
    }

    private Tag createTag(String name, String description, String detailedDescription) {
        return new Tag()
            .name(name)
            .description(description)
            .externalDocs(new ExternalDocumentation()
                .description(detailedDescription));
    }
} 