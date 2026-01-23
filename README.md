# Library Management API

Spring Boot REST API for managing books and authors in a library system  
**Homework project** from Coding Shuttle — Spring Testing module (Jan 2026)

**Overall JaCoCo coverage: ~90%**  
(Instructions 90%, Branches 80%, Lines ~90%+)

Built with clean architecture, custom JPA queries, DTO validation,  
unit tests (100% on repositories & services), and full integration tests with Testcontainers.

## What the project covers
- Two main entities: Book and Author
  - Proper relationship (@ManyToOne + bidirectional @OneToMany)
  - Auditing fields (@CreatedDate / @LastModifiedDate)
  - Schema defined via JPA annotations (no manual SQL)

- Basic CRUD operations for both Book and Author

- Custom query methods (Spring Data JPA derived + one @Query with JOIN FETCH)
  - Find books by title (partial, case-insensitive)
  - Find books published on or after a certain year
  - Find all books by a specific author
  - Find authors by name (partial, case-insensitive)

- REST endpoints (/api/books and /api/authors)
  - Create, Read (single + list), Update, Delete
  - Custom search endpoints (title, author ID, year)

- Custom validation logic in DTOs
  - @NotBlank, @Size, @Min/@Max, @Pattern (for ISBN format)

- Unit tests
  - Repositories: @DataJpaTest + H2 → 100% coverage
  - Services: Mockito + real ModelMapper → 100% coverage

- Integration tests
  - Full API endpoints tested with @SpringBootTest + Testcontainers (real MySQL)
  - WebTestClient used to verify HTTP status, JSON responses, relationships

- JaCoCo coverage report generated
  - Command: `mvn clean test jacoco:report`
  - Full interactive report: `target/site/jacoco/index.html`
  - <img width="2468" height="392" alt="image" src="https://github.com/user-attachments/assets/c4abcbe7-f484-464f-90a9-c76780e4a708" />

  - Overall ~90% (services & controllers very high, minor gaps in advice/main class)

## How to run locally

1. Make sure MySQL is running (XAMPP or Docker)
2. Create database `library_db`
3. Update `application.yml` or `application.properties` with your DB credentials
4. Build & start:

```bash
mvn clean install
mvn spring-boot:run
