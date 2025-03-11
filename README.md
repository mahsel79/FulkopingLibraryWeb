# Fulkoping Library Web Application

A comprehensive library management system built with Java and Spring framework.

## Project Structure

```
FulkopingLibraryWeb/
├── docs/ - Project documentation
│   ├── error-handling.md
│   ├── refactoring-plan.md
│   ├── spring-integration.md
│   └── testing.md
├── src/
│   ├── main/
│   │   ├── java/ - Main application code
│   │   │   └── se/fulkopinglibraryweb/
│   │   │       ├── aspects/ - AOP aspects
│   │   │       ├── cache/ - Caching implementation
│   │   │       ├── config/ - Spring configuration
│   │   │       ├── controllers/ - Web controllers
│   │   │       ├── filters/ - HTTP filters
│   │   │       ├── models/ - Data models
│   │   │       ├── repository/ - Data repositories
│   │   │       ├── security/ - Security implementation
│   │   │       ├── services/ - Business logic
│   │   │       ├── servlets/ - Legacy servlets
│   │   │       ├── utils/ - Utility classes
│   │   │       └── validation/ - Validation logic
│   │   └── resources/ - Configuration files
│   └── test/ - Unit and integration tests
├── webapp/ - Web application resources
│   ├── css/ - Stylesheets
│   ├── js/ - JavaScript files
│   ├── WEB-INF/ - Web configuration
│   └── *.jsp - View templates
├── pom.xml - Maven build configuration
└── README.md - This file
```

## Key Components

### Core Services
- **LibraryService**: Main business logic
- **SearchService**: Unified search implementation
- **CacheService**: Two-level caching system

### Data Layer
- **BookRepository**: Book data access
- **MagazineRepository**: Magazine data access
- **MediaRepository**: Media data access

### Web Layer
- **BookController**: REST endpoints for books
- **MagazineController**: REST endpoints for magazines
- **MediaController**: REST endpoints for media

### Security
- **SecurityFilter**: Authentication and authorization
- **RateLimiter**: API rate limiting
- **CredentialEncryption**: Password encryption

## Build and Run

1. Install dependencies:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

3. Access the application at:
```
http://localhost:8080
```

## Configuration

Key configuration files:
- `src/main/resources/application.properties` - Main application config
- `webapp/WEB-INF/web.xml` - Web application config
- `src/main/java/se/fulkopinglibraryweb/config/` - Spring configuration classes

## Documentation

See the `docs/` directory for:
- Error handling guidelines
- Refactoring plans
- Spring integration details
- Testing strategies
