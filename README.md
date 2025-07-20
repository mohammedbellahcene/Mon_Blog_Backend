# Blog API Backend

Ce dossier contient le backend de l’application de blog, développé avec **Spring Boot** (Java 17) Maven.

## 🚀 Fonctionnalités principales
- Authentification JWT (inscription, connexion, refresh)
- Gestion des articles (CRUD, recherche, filtrage par thème, auteur, tags)
- Gestion des utilisateurs, rôles et statistiques
- Modération des commentaires
- Notifications, réactions (like/dislike)
- API RESTful documentée avec Swagger
- Support MySQL et MongoDB (statistiques)

## 🗂️ Structure du projet

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/blog/api/
│   │   │   ├── controller/   # Contrôleurs REST (API)
│   │   │   ├── service/      # Logique métier (services)
│   │   │   ├── repository/   # Accès aux données (JPA/Mongo)
│   │   │   ├── entity/       # Entités JPA (User, Post, Comment, ...)
│   │   │   ├── dto/          # Objets de transfert de données (DTO)
│   │   │   ├── config/       # Configuration Spring, sécurité, Swagger
│   │   │   ├── security/     # Sécurité JWT, rôles, filtres
│   │   │   └── BlogApplication.java # Point d'entrée Spring Boot
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application.yml
│   └── test/
│       └── java/com/blog/api/
│           ├── controller/   # Tests des contrôleurs
│           └── service/      # Tests des services
├── pom.xml                   # Dépendances Maven
├── Dockerfile                # Image Docker du backend
└── package.json              # (utilisé pour dotenv-cli en dev)
```

## ⚙️ Démarrage rapide

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL (ou autre DB compatible)
- (Optionnel) Docker

### Lancer en local
```bash
cd backend
# Configurer les variables d'environnement (voir ci-dessous)
mvn clean package
java -jar target/spring-blog-api-0.0.1-SNAPSHOT.jar
```

### Avec Docker
```bash
docker build -t blog-backend .
docker run -p 8080:8080 --env-file ../.env blog-backend
```

## 🔑 Configuration (extraits)
Variables à définir dans `.env` ou dans votre environnement :
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/blog_db
SPRING_DATASOURCE_USERNAME=bloguser
SPRING_DATASOURCE_PASSWORD=blogpass
JWT_SECRET=une_chaine_secrete_longue
```

## 📚 Documentation API
- Swagger UI : http://localhost:8080/swagger-ui.html
- OpenAPI : http://localhost:8080/v3/api-docs

## 🧩 Principaux dossiers
- **controller/** : endpoints REST (ex : AuthController, PostController, CommentController...)
- **service/** : logique métier (ex : AuthService, PostService, SearchService...)
- **repository/** : accès BDD (JPA/Mongo)
- **entity/** : modèles de données (User, Post, Comment...)
- **dto/** : objets de transfert (requêtes/réponses API)
- **config/** : configuration sécurité, Swagger, etc.
- **security/** : JWT, rôles, UserDetails

## 🧪 Tests
- Tests unitaires et d’intégration dans `src/test/java/com/blog/api/controller/` et `service/`

## 📝 Auteur & Licence
- Projet réalisé avec fierté pour un blog moderne et sécurisé.
- Licence : MIT (à adapter selon votre besoin) 