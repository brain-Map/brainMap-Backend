# BrainMap Backend - Complete Project Structure & Workflow

## Project Overview
BrainMap is a Spring Boot 3.5.3 backend application built with Java 21, designed as a community platform that supports project collaboration, expert mentoring, and service marketplace functionality.

## Technology Stack

### Core Technologies
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: PostgreSQL (Production), H2 (Testing)
- **Security**: Spring Security with JWT Authentication
- **ORM**: Spring Data JPA with Hibernate

### Key Dependencies
- **Mapping**: MapStruct 1.6.3
- **Boilerplate Reduction**: Lombok 1.18.36
- **JWT Handling**: Auth0 Java JWT 4.5.0
- **Validation**: Spring Boot Starter Validation
- **API Documentation**: SpringDoc OpenAPI 2.2.0

## Project Structure

```
brainMap-Backend/
│
├── .github/workflows/          # GitHub Actions
│   └── todo-to-issue.yml      # Automatically creates issues from TODO comments
│
├── .mvn/wrapper/              # Maven wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/com/app/brainmap/
│   │   │   ├── BrainMapApplication.java          # Main Spring Boot application class
│   │   │   │
│   │   │   ├── config/                           # Configuration classes
│   │   │   │   └── SecurityConfig.java          # Spring Security configuration
│   │   │   │
│   │   │   ├── controllers/                     # REST API endpoints
│   │   │   │   ├── AuthController.java          # Authentication endpoints
│   │   │   │   ├── CommunityPostController.java # Community post management
│   │   │   │   ├── CommunityTagController.java  # Tag management
│   │   │   │   ├── ErrorController.java         # Global exception handling
│   │   │   │   └── UserController.java          # User management
│   │   │   │
│   │   │   ├── domain/                          # Domain models and DTOs
│   │   │   │   ├── entities/                    # JPA entities
│   │   │   │   │   ├── User.java               # User entity
│   │   │   │   │   ├── CommunityPost.java      # Community post entity
│   │   │   │   │   ├── CommunityComment.java   # Comment entity
│   │   │   │   │   ├── CommunityReply.java     # Reply entity
│   │   │   │   │   ├── CommunityTag.java       # Tag entity
│   │   │   │   │   ├── Project.java            # Project entity
│   │   │   │   │   ├── ProjectTask.java        # Project task entity
│   │   │   │   │   ├── ProjectContributor.java # Project contributor entity
│   │   │   │   │   ├── Service.java            # Service entity
│   │   │   │   │   ├── ServicePackage.java     # Service package entity
│   │   │   │   │   ├── Appointment.java        # Appointment entity
│   │   │   │   │   ├── Payment.java            # Payment entity
│   │   │   │   │   ├── Inquiry.java            # Inquiry entity
│   │   │   │   │   └── Notification.java       # Notification entity
│   │   │   │   │
│   │   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   │   │   ├── UserDto.java            # User response DTO
│   │   │   │   │   ├── CreateUserDto.java      # User creation DTO
│   │   │   │   │   ├── CommunityPostDto.java   # Post response DTO
│   │   │   │   │   ├── CommunityPostAuthorDto.java # Post author DTO
│   │   │   │   │   ├── CreateCommunityPostRequestDto.java # Post creation DTO
│   │   │   │   │   ├── CommunityTagResponse.java # Tag response DTO
│   │   │   │   │   ├── CreateCommunityTagRequest.java # Tag creation DTO
│   │   │   │   │   └── ApiErrorResponse.java   # Error response DTO
│   │   │   │   │
│   │   │   │   ├── CreateUser.java              # User creation domain model
│   │   │   │   ├── CreateCommunityPostRequest.java # Post creation domain model
│   │   │   │   ├── UserRoleType.java           # User role enumeration
│   │   │   │   └── CommunityPostType.java      # Post type enumeration
│   │   │   │
│   │   │   ├── mappers/                         # MapStruct mappers
│   │   │   │   ├── UserMapper.java             # User entity/DTO mapping
│   │   │   │   ├── CommunityPostMapper.java    # Post entity/DTO mapping
│   │   │   │   └── CommunityTagMapper.java     # Tag entity/DTO mapping
│   │   │   │
│   │   │   ├── repositories/                    # Data access layer
│   │   │   │   ├── UserRepository.java         # User data access
│   │   │   │   ├── CommunityPostRepository.java # Post data access
│   │   │   │   ├── CommunityCommentRepository.java # Comment data access
│   │   │   │   ├── CommunityReplyRepository.java # Reply data access
│   │   │   │   └── CommunityTagRepository.java # Tag data access
│   │   │   │
│   │   │   ├── security/                        # Security components
│   │   │   │   ├── JwtAuthenticationFilter.java # JWT request filter
│   │   │   │   ├── JwtAuthenticationProvider.java # JWT authentication provider
│   │   │   │   ├── JwtAuthenticationToken.java # JWT authentication token
│   │   │   │   └── JwtUserDetails.java         # JWT user details
│   │   │   │
│   │   │   └── services/                        # Business logic layer
│   │   │       ├── UserService.java            # User service interface
│   │   │       ├── CommunityPostService.java   # Post service interface
│   │   │       ├── CommunityTagService.java    # Tag service interface
│   │   │       └── impl/                       # Service implementations
│   │   │           ├── UserServiceImpl.java    # User service implementation
│   │   │           ├── CommunityPostServiceImpl.java # Post service implementation
│   │   │           └── CommunityTagServiceImpl.java # Tag service implementation
│   │   │
│   │   └── resources/
│   │       ├── application.properties          # Main configuration
│   │       └── application-development.properties # Development profile (gitignored)
│   │
│   └── test/
│       ├── java/com/app/brainmap/
│       │   └── BrainMapApplicationTests.java   # Basic application tests
│       └── resources/
│           └── application.properties          # Test configuration (H2 database)
│
├── target/                                     # Build output directory
├── pom.xml                                     # Maven configuration
├── mvnw, mvnw.cmd                             # Maven wrapper scripts
├── README.md                                   # Project documentation
├── .gitignore                                  # Git ignore rules
└── .gitattributes                             # Git attributes configuration
```

## Application Workflow

### 1. Authentication & Security Flow

#### JWT Authentication Process:
1. **Token Extraction**: `JwtAuthenticationFilter` extracts Bearer token from Authorization header
2. **Token Validation**: `JwtAuthenticationProvider` validates JWT using HMAC256 algorithm
3. **User Details**: Creates `JwtUserDetails` object with userId and email from token claims
4. **Security Context**: Sets authenticated user in Spring Security context

#### Security Configuration:
- **CORS**: Allows requests from `http://localhost:3000`
- **CSRF**: Disabled for stateless JWT authentication
- **Authorization**: Currently permits all requests (TODO: implement proper authorization)
- **Excluded Paths**: `/api/v1/auth/test` and `/api/v1/users/test` bypass authentication

### 2. API Endpoints Structure

#### Authentication API (`/api/v1/auth`)
- `GET /api/v1/auth` - Get authenticated user details
- `GET /api/v1/auth/test` - Test endpoint (no auth required)

#### User Management API (`/api/v1/users`)
- `POST /api/v1/users` - Create new user
- `GET /api/v1/users/test` - Test endpoint

#### Community Posts API (`/api/v1/posts`)
- `GET /api/v1/posts` - Get all posts (optional tagId filter)
- `POST /api/v1/posts` - Create new community post

#### Community Tags API (`/api/v1/tags`)
- `GET /api/v1/tags` - Get all tags with post counts
- `POST /api/v1/tags` - Create new tags
- `DELETE /api/v1/tags/{id}` - Delete tag by ID
- `POST /api/v1/tags/test` - Create tags for post (returns UUIDs)

### 3. Data Flow Architecture

#### Request Processing Flow:
1. **HTTP Request** → REST Controller
2. **DTO Validation** → Bean Validation annotations
3. **Authentication** → JWT filter chain
4. **Business Logic** → Service layer
5. **Data Access** → Repository layer (Spring Data JPA)
6. **Response Mapping** → MapStruct mappers
7. **HTTP Response** → JSON serialization

#### Entity Relationships:
- **User** ← One-to-Many → **CommunityPost**, **CommunityComment**, **CommunityReply**
- **CommunityPost** ← Many-to-Many → **CommunityTag**
- **CommunityPost** ← One-to-Many → **CommunityComment**
- **CommunityComment** ← One-to-Many → **CommunityReply**
- **User** ← One-to-Many → **Project** (as owner)
- **Project** ← Many-to-Many → **User** (through ProjectContributor)
- **Service** ← One-to-Many → **ServicePackage**
- **User** ← Many-to-Many → **Service** (through Appointment, Payment)

### 4. Database Design

#### Core Tables:
- **users**: User profiles and authentication
- **community_posts**: Discussion posts, projects, help requests
- **community_comments**: Comments on posts
- **community_reply**: Replies to comments
- **community_tags**: Categorization tags
- **community_post_tags**: Many-to-many relationship table

#### Extended Tables (Future Features):
- **projects**: Collaborative projects
- **project_tasks**: Project task management
- **project_contributors**: Project team members
- **services**: Expert services offered
- **service_packages**: Service pricing tiers
- **appointments**: Service bookings
- **payments**: Payment transactions
- **inquiries**: Customer support tickets
- **notification**: User notifications

### 5. Error Handling Strategy

#### Global Exception Handling:
- `@ControllerAdvice` in `ErrorController`
- **EntityNotFoundException** → 404 Not Found
- **IllegalArgumentException** → 400 Bad Request
- **Generic Exception** → 500 Internal Server Error
- Standardized `ApiErrorResponse` format

### 6. Configuration Management

#### Application Profiles:
- **Default**: Basic configuration
- **Development**: Local development settings (database credentials)
- **Test**: H2 in-memory database for testing

#### Key Configuration Properties:
- **Database**: PostgreSQL driver, connection pooling
- **JPA**: Hibernate DDL auto-creation, SQL logging
- **Security**: JWT secret key
- **CORS**: Frontend origin configuration

### 7. Build & Deployment

#### Maven Build Process:
1. **Compilation**: Java 21 source compilation
2. **Annotation Processing**: Lombok and MapStruct code generation
3. **Testing**: JUnit 5 test execution
4. **Packaging**: Executable JAR creation
5. **Docker**: Ready for containerization

#### Generated Code:
- **MapStruct Implementations**: Auto-generated in `target/generated-sources/annotations/`
- **Lombok**: Compile-time generation of getters, setters, builders

## Development Workflow

### 1. Local Development Setup:
```bash
# Clone repository
git clone https://github.com/brain-Map/brainMap-Backend.git
cd brainMap-Backend

# Configure database in application-development.properties
# Build and run
mvn clean install
mvn spring-boot:run
```

### 2. Testing Strategy:
- **Unit Tests**: Service layer testing
- **Integration Tests**: Repository layer testing with H2
- **API Tests**: Controller endpoint testing

### 3. Code Quality:
- **Lombok**: Reduces boilerplate code
- **MapStruct**: Type-safe mapping between layers
- **Validation**: Bean Validation annotations
- **Exception Handling**: Centralized error handling

### 4. Future Enhancements:
- Complete authentication system implementation
- Project management features
- Service marketplace functionality
- Real-time notifications
- File upload capabilities
- Advanced search and filtering
- Email integration
- Payment processing integration

## API Documentation
The application includes SpringDoc OpenAPI integration for automatic API documentation generation. Once running, visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Security Considerations
- JWT tokens are validated server-side
- Passwords should be encrypted (not yet implemented)
- CORS configuration restricts frontend origins
- Input validation prevents malicious data
- SQL injection prevention through JPA/Hibernate

This Spring Boot application follows clean architecture principles with clear separation of concerns, making it maintainable and scalable for future enhancements.
