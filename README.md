# Smart bio – Backend

Backend til **Smart bio**, et system til en lokal biograf. Projektet er bygget med Spring Boot og kan køres både lokalt og i Docker sammen med en MySQL-database.

Selve siden kan ses her: [web app](https://frontend.gentlesmoke-03ba2725.swedencentral.azurecontainerapps.io/#/)

## Baggrund

Systemet tager udgangspunkt i en lille, lokal biograf:

- To biografsale
- Fire medarbejdere
- Salg af billetter, popcorn og andre varer i caféen

Målet er at give biografen ét samlet system, som understøtter den daglige drift.

## Tech stack

| Område | Teknologi |
|---|---|
| Sprog | Java 25 |
| Framework | Spring Boot 4.1.1 |
| Web | Spring Web MVC |
| Views | Thymeleaf |
| Database-adgang | Spring Data JPA |
| Validering | Spring Boot Validation |
| Sikkerhed | Spring Security |
| Database (drift/Docker) | MySQL 8.0 |
| Database (udvikling) | H2 (inkl. H2 Console) |
| Build | Maven (Maven Wrapper medfølger) |
| Containere | Docker og Docker Compose |

Se også mappen `docs/` for den uddybende dokumentation af tech stack.

## Projektstruktur

```
.
├── .github/workflows/   # CI/CD-workflows (GitHub Actions)
├── docs/                # Projektdokumentation
├── src/                 # Applikationens kildekode og tests
├── .env-example         # Eksempel på miljøvariabler til databasen
├── Dockerfile           # Multi-stage build af applikationen
├── compose.yaml         # Backend + MySQL
├── mvnw / mvnw.cmd      # Maven Wrapper
└── pom.xml              # Maven-konfiguration og afhængigheder
```

## Kom i gang

### Forudsætninger

- Docker og Docker Compose
- (Valgfrit, til lokal kørsel uden Docker) JDK 25

### Kør med Docker

1. Opret en `.env`-fil ud fra eksemplet:

   ```bash
   cp .env-example .env
   ```

2. `compose.yaml` bruger et eksternt netværk ved navn `app-net`. Opret det, hvis det ikke allerede findes:

   ```bash
   docker network create app-net
   ```

3. Byg og start:

   ```bash
   docker compose up --build
   ```

Backenden startes med Spring-profilen `docker` og er derefter tilgængelig på:

- **Backend:** http://localhost:8081 (containerens port 8080)
- **MySQL:** `localhost:3307` (containerens port 3306), database `kinodb`

Begge porte er kun bundet til `127.0.0.1`, så de ikke er tilgængelige udefra.

Data gemmes i Docker-volumen `kinodb_volume` og overlever derfor, at containerne stoppes.

### Kør lokalt uden Docker

```bash
./mvnw spring-boot:run
```

Til kørsel lokalt brug H2-databasen som er sat op.

## Konfiguration

Databasens opsætning styres af miljøvariabler (se `.env-example`):

| Variabel | Beskrivelse |
|---|---|
| `MYSQL_ROOT_PASSWORD` | Root-adgangskode til MySQL |
| `MYSQL_DATABASE` | Databasenavn (`kinodb`) |
| `MYSQL_USER` |  Databasebruger |
| `MYSQL_PASSWORD` |  Databaseadgangskode |

## Tests

```bash
./mvnw test
```

## Docker-build

`Dockerfile` bruger to trin:

1. **Build:** Maven 3.9 med Temurin JDK 25 bygger en jar-fil (`mvn clean package -DskipTests`).
2. **Runtime:** Jar-filen kopieres over i et lille `eclipse-temurin:25-jre-alpine`-image.

## Funktioner
> - Visning af film og forestillinger
> - Billetbooking
> - Roller og adgang for medarbejdere
> - Find billetter (QR-Kode)


