# Brainmap Spring Boot Application

## Getting Started

Follow the steps below to clone this repository and run the Spring Boot Maven application locally.

### Clone the Repository

```bash
git clone https://github.com/brain-Map/brainMap-Backend.git
cd brainMap-Backend
````

### Configure the Application

Before starting the application, update the database configurations in:

```src/main/resources/application.properties```

```java
# Replace following with your configuration

spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=your_db_username
spring.datasource.password=your_password
```

Replace the placeholder values with your actual database connection details, such as URL, username, password, and driver class.

### Build and Run the Application

Use Maven to build and start the app:

```bash
mvn clean install
mvn spring-boot:run
```

This will compile the project and start the embedded server.

---

## Project Directory Structure

```
src/main/java/com/app/brainmap/
│
├── services/
│   └── impl/                     # Service interface implementations
│
├── domain/
│   ├── dto/                      # Data Transfer Objects (DTOs) implemented as Java Records
│   └── entities/                 # JPA entities representing database tables
│
├── mappers/
│   └── impl/                    # Mapper implementations to convert between DTOs and Entities
│
├── repositories/                # Spring Data JPA repositories for data access
```

### Key Concepts

* **DTOs** are created using [Java Records](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Record.html), which provide a concise way to define immutable data carriers.
* **Mappers** handle the conversion between DTOs and Entities to decouple the persistence layer from the API layer, typically implemented using a mapping framework or manually.
* **Services** contain business logic and interact with repositories for data operations.
* **Repositories** use Spring Data JPA to abstract database interactions with CRUD methods.

---

## Additional Notes

* Make sure your database is running and accessible with the credentials specified in `application.properties`.

---