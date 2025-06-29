package com.blog.api;

import com.blog.api.entity.Theme;
import com.blog.api.repository.ThemeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ThemeRepository themeRepository) {
        return args -> {
            if (themeRepository.count() == 0) {
                List<Theme> defaultThemes = Arrays.asList(
                    createTheme("General", "Articles généraux et divers"),
                    createTheme("Technologie", "Articles sur les dernières innovations technologiques"),
                    createTheme("Voyage", "Découvertes et expériences de voyage à travers le monde"),
                    createTheme("Cuisine", "Recettes, astuces culinaires et découvertes gastronomiques"),
                    createTheme("Sport", "Actualités sportives et conseils d'entraînement"),
                    createTheme("Culture", "Art, littérature, cinéma et musique"),
                    createTheme("Santé", "Bien-être, nutrition et conseils santé"),
                    createTheme("Education", "Ressources éducatives et méthodes d'apprentissage")
                );
                
                themeRepository.saveAll(defaultThemes);
            }
        };
    }

    private Theme createTheme(String name, String description) {
        Theme theme = new Theme();
        theme.setName(name);
        theme.setDescription(description);
        return theme;
    }
} 