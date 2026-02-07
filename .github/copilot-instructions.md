# Copilot Instructions for Fake News Reporter

A Spring Boot 3.2.0 application for reporting and managing fake news sources. Users submit reports anonymously, and admins review and approve them for public visibility.

## Build, Test, and Run

### Run Application
```bash
# Local development (H2 in-memory)
mvn spring-boot:run

# With specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Docker (PostgreSQL)
docker-compose up --build
```

### Testing
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClassName

# Run specific test method
mvn test -Dtest=ClassName#methodName
```

### Build
```bash
# Build JAR
mvn clean package

# Verify without tests
mvn verify -DskipTests
```

### Database Access
- **H2 Console** (local): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:fakenews`
  - Username: `sa`
  - Password: (empty)
- **Admin Login**: username `admin` / password `admin123`

## Architecture

### Multi-Profile Configuration
The application uses Spring profiles to support different database backends:

- **`local`** (default): H2 in-memory for rapid development
- **`prod`**: PostgreSQL for production
- **`test`**: H2 with test-specific settings

Configuration files: `application.yml`, `application-local.yml`, `application-prod.yml`, `application-test.yml`

**Critical**: When modifying database-related code, test against both H2 (local) and PostgreSQL (docker-compose) to ensure compatibility.

### Entity Model
```
User (users)
├── id, username (unique), password (BCrypt)
├── role (e.g., "ROLE_ADMIN"), enabled
└── No FK relationship with reports

FakeNewsReport (fake_news_reports)
├── id, newsSource, url
├── category (Politics|Health|Science|Technology|Entertainment|Finance)
├── description, reportedAt
├── approved (boolean), approvedAt, approvedBy (username string)
└── Reports are anonymous submissions

Comment (comments)
├── id, content, createdAt
├── report_id (FK to fake_news_reports)
└── user_id (FK to users)
```

**Key design**: Reports are soft-approved via boolean flag, not moved between tables. This maintains audit trail and simplifies querying.

### Security Configuration
Located in `SecurityConfig.java`:

- **Public endpoints**: `/`, `/report`, `/reports`, `/reports/*/comments`, static resources
- **Admin endpoints**: `/admin/**` requires `ROLE_ADMIN`
- **Authentication**: Custom `UserDetailsService` with BCrypt
- **CSRF**: Enabled for all forms (Thymeleaf includes tokens automatically with `th:action`)

When adding new endpoints, update `.requestMatchers()` in the security filter chain.

### Data Flow Patterns

**Report Submission**:
1. User submits form → `HomeController.submitReport()` (POST `/report`)
2. Validation via `@Valid ReportForm`
3. Service creates `FakeNewsReport` with `approved=false`
4. Repository saves entity
5. Redirect to confirmation page

**Admin Approval**:
1. Admin views pending → `AdminController.dashboard()` (GET `/admin/dashboard`)
2. Admin approves → `AdminController.approveReport()` (POST `/admin/approve/{id}`)
3. Service updates: `approved=true`, `approvedAt`, `approvedBy`
4. Repository saves
5. Redirect to dashboard

### Data Initialization
`DataInitializer.java` runs on startup (`@PostConstruct`):
- Creates default admin user if not exists
- Inserts sample reports for demo

**When adding initialization logic**: Use idempotency checks like `if (!userRepository.existsByUsername(...))` to prevent duplicates on restarts.

### Thymeleaf Templates
Simple layout pattern without framework:
- `index.html` - Homepage with verified reports
- `reports.html` - Full list of verified reports
- `report-form.html` - Public submission form
- `login.html` - Admin login
- `admin/dashboard.html` - Admin review interface

Patterns used: `th:each`, `th:text`, `th:href`, `th:action`, `th:object`, `th:field`, `sec:authorize` (Spring Security extras)

## Key Conventions

### Adding a New Report Category
1. Update `FakeNewsReport.java` - add to category validation/enum
2. Update `report-form.html` - add option to category dropdown
3. Update `DataInitializer.java` - optionally add sample data
4. No manual migration needed (using `ddl-auto: update`)

### Adding Admin Endpoints
1. Add method to `AdminController.java`
2. Verify `/admin/**` pattern in `SecurityConfig.java` covers it
3. Create Thymeleaf template in `templates/admin/`
4. Ensure CSRF token in forms (automatic with `th:action`)

### Database Schema Changes
- **Local/Dev**: Auto-updates with `ddl-auto: update`
- **Production**: Currently uses `ddl-auto: update` (be cautious with destructive changes)
- Consider Flyway/Liquibase for production migrations (not currently configured)

### Adding Maven Dependencies
Edit `pom.xml`, then run:
```bash
mvn dependency:resolve  # verify
mvn spring-boot:run     # test locally
```

## CI/CD and Versioning

### Commit Message Format
Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>
```

**Version bumping**:
- `feat:` → Minor (1.0.0 → 1.1.0)
- `fix:`, `perf:`, `refactor:` → Patch (1.0.0 → 1.0.1)
- `feat!:` or `BREAKING CHANGE:` → Major (1.0.0 → 2.0.0)
- `docs:`, `chore:`, `test:`, `ci:` → No release

**Examples**:
```bash
git commit -m "feat(reports): add pagination to reports list"
git commit -m "fix(auth): resolve session timeout issue"
```

### Workflows
- **CI** (`ci.yml`): Runs on all pushes/PRs - tests, quality checks, build verification
- **Build & Push** (`build-push.yml`): On main branch - semantic versioning, Docker build, GHCR push, release creation

**Docker images**: `ghcr.io/automatica-cluj/demo-project` with tags: `latest`, `v{version}`, `sha-{commit}`, `main`, `{timestamp}`

## Important Constraints

- **Java 17 required**: Spring Boot 3.2.0 minimum requirement
- **Multi-database compatibility**: Avoid database-specific SQL (H2 + PostgreSQL)
- **Anonymous reporting**: No FK between User and FakeNewsReport - maintain this design
- **Default credentials**: For development only - must change in production
