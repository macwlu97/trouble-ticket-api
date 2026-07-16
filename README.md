# Trouble Ticket API

Implementacja backendowa API zgodna ze specyfikacją TMF621 Trouble Ticket przygotowana w ramach zadania.

## Cel projektu

Celem projektu jest implementacja działającej usługi backendowej na podstawie kontraktu API opisanego w specyfikacji OpenAPI. Projekt demonstruje podejście do budowy produkcyjnej aplikacji z uwzględnieniem jakości kodu, testów i dokumentacji.

## Technologie

- **Java 21**
- **Spring Boot 3.5.16**
- **Spring Data JPA**
- **PostgreSQL 16**
- **Flyway** - migracje bazy danych
- **Testcontainers** - testy integracyjne
- **Spring Security** - uwierzytelnianie JWT
- **OpenAPI Generator** - generacja modeli z kontraktu
- **SpringDoc OpenAPI** - dokumentacja Swagger UI

## Architektura

Projekt zastosował architekturę hexagonal (ports and adapters):

- **Domain Layer** - logika biznesowa, encje, reguły domenowe
- **Application Layer** - use cases, serwis aplikacyjny
- **Infrastructure Layer** - baza danych, konfiguracja
- **API Layer** - kontrolery REST, mapowanie DTO

### Kluczowe decyzje architektoniczne

1. **Multi-tenancy** - izolacja danych na poziomie tenant_id z Bearer tokenu
2. **Idempotentność** - operacja create zwraca istniejący zasób dla tego samego (tenantId, externalId)
3. **Domain-driven design** - logika biznesowa w encjach domenowych (np. przejścia statusów)
4. **Testcontainers** - testy integracyjne z prawdziwą bazą PostgreSQL

## Uruchomienie projektu

### Wymagania

- Docker i Docker Compose
- Java 21
- Maven 3.8+

### Sposób 1: Docker Compose (zalecane)

```bash
# Uruchomienie pełnego środowiska (aplikacja + baza danych)
docker-compose up --build

# Aplikacja będzie dostępna na http://localhost:8080/api/v1
# Swagger UI: http://localhost:8080/api/v1/swagger-ui
```

### Sposób 2: Lokalne uruchomienie

```bash
# Uruchomienie bazy danych
docker-compose up postgres

# Uruchomienie aplikacji
./mvnw spring-boot:run

# Aplikacja będzie dostępna na http://localhost:8080/api/v1
```

### Testy

```bash
# Wszystkie testy
./mvnw test

# Tylko testy integracyjne
./mvnw test -Dtest=TroubleTicketApiIntegrationTest
```

## Specyfikacja API

### Endpointy

- `POST /api/v1/troubleTicket` - utworzenie zgłoszenia
- `GET /api/v1/troubleTicket` - lista zgłoszeń (bez paginacji w v1)
- `GET /api/v1/troubleTicket/{id}` - szczegóły zgłoszenia
- `PATCH /api/v1/troubleTicket/{id}` - zamknięcie zgłoszenia
- `POST /api/v1/troubleTicket/{id}/note` - dodanie notatki

### Zasady kontraktu

- Wszystkie endpointy wymagają Bearer token
- Tenant scope wynika wyłącznie z kontekstu uwierzytelnienia
- Create przyjmuje wyłącznie status `new`
- Publiczny update statusu dopuszcza wyłącznie status `closed`
- Idempotentność na podstawie `(tenantId, externalId)`

## Przyjęte założenia i ograniczenia

### Założenia

1. **Walidacja serviceId** - w v1 przyjęto uproszczone podejście - wszystkie serviceId > 0 są uznawane jako poprawne. W środowisku produkcyjnym wymagana byłaby integracja z katalogiem usług.
2. **Symulacja przejść statusów** - statusy `acknowledged`, `inProgress`, `resolved` są ustawiane bezpośrednio w bazie danych (symulacja SOZ). W środowisku produkcyjnym byłyby zmieniane przez system obsługi zgłoszeń.
3. **Brak paginacji** - zgodnie ze specyfikacją v1, endpoint listy nie wspiera paginacji ani filtrowania.
4. **Bezstanowy JWT** - aplikacja nie weryfikuje tokena z Keycloak, tylko extrahuje tenant_id. W środowisku produkcyjnym wymagana byłaby pełna integracja z OAuth2/OIDC.

### Ograniczenia

1. **Brak walidacji ServiceNotFound** - walidacja istnienia usługi (serviceId) nie została zaimplementowana w v1.
2. **Brak cache** - brak mechanizmów cache'owania - w środowisku produkcyjnym rozważono by Redis.
3. **Brak audytu** - brak logowania zmian statusów - w środowisku produkcyjnym wymagana byłaby tabela audytu.
4. **Brak rate limiting** - brak ograniczeń liczby żądań - w środowisku produkcyjnym wymagany byłby rate limiter.

## Struktura bazy danych

```sql
CREATE TABLE trouble_ticket
(
    id VARCHAR(255) PRIMARY KEY,

    tenant_id VARCHAR(100) NOT NULL, -- Added for multi-tenancy core isolation

    external_id VARCHAR(255) NOT NULL, -- REMOVED: GLOBAL UNIQUE CONSTRAINT

    service_id BIGINT NOT NULL,

    description TEXT NOT NULL,

    status VARCHAR(50) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Composite unique constraint ensuring externalId is unique ONLY within a specific tenant scope
    CONSTRAINT uk_ticket_tenant_external
        UNIQUE (tenant_id, external_id)
);

CREATE TABLE note
(
    id UUID PRIMARY KEY,

    trouble_ticket_id VARCHAR(255) NOT NULL,

    text TEXT NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_note_ticket
        FOREIGN KEY (trouble_ticket_id)
            REFERENCES trouble_ticket(id)
            ON DELETE CASCADE
);

-- Index for optimized queries filtered by tenantId and externalId combinations
CREATE UNIQUE INDEX idx_ticket_tenant_external
    ON trouble_ticket(tenant_id, external_id);

-- Performance index for high-throughput tenant dashboard listings
CREATE INDEX idx_ticket_tenant
    ON trouble_ticket(tenant_id);

CREATE INDEX idx_note_ticket
    ON note(trouble_ticket_id);

```

## Testy

Projekt zawiera trzy poziomy testów:

1. **Unit tests** - testy domenowe (domain/model)
2. **Service tests** - testy warstwy aplikacji z mockami
3. **Integration tests** - testy end-to-end z Testcontainers

### Pokrycie testów

- Domain logic: ✅ pełne pokrycie
- Use cases: ✅ podstawowe scenariusze
- API controllers: ✅ podstawowe scenariusze
- Negative scenarios: ⚠️ częściowe (możliwość rozszerzenia)

## Konfiguracja

### Zmienne środowiskowe

```bash
# Baza danych
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/trouble_ticket
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# OAuth2 (opcjonalne dla lokalnego uruchomienia)
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER-URI=http://localhost:8080/realms/trouble-ticket-realm
```

### Profile

- `dev` - środowisko deweloperskie (domyślne)
- `test` - środowisko testowe
- `prod` - środowisko produkcyjne

## Dokumentacja API

Swagger UI dostępny pod adresem: `http://localhost:8080/api/v1/swagger-ui`

OpenAPI specyfikacja: `http://localhost:8080/api/v1/api-docs`

## Problemy znane i przyszłe usprawnienia

### Znane problemy

1. Duplikaty zależności w pom.xml (testcontainers) - do usunięcia
2. Brak nagłówka Location w odpowiedziach POST - wymagane przez specyfikację

### Przyszłe usprawnienia

1. Pełna integracja z Keycloak/OAuth2
2. Walidacja serviceId z katalogiem usług
3. Paginacja i filtrowanie listy zgłoszeń
4. Mechanizm cache'owania (Redis)
5. Audyt zmian statusów
6. Rate limiting
7. Metrics i monitoring actuator
8. Async processing dla długich operacji

