<<<<<<< HEAD
Job Board

A local job vacancy manager built with Java 21 and Spring Boot. This is a junior Java portfolio project with a REST API, a browser interface and a relational database.

Features

The application lets you create, view, edit and delete vacancies. Each vacancy contains a job title, company, location, description and required years of experience. You can search by title or company without matching letter case.

The server validates required fields, text lengths and experience between 0 and 50 years. Invalid requests return 400, missing vacancies return 404, successful creation returns 201 and deletion returns 204.

Technology

Java 21, Spring Boot 3.5.16, Spring Web, Spring Data JPA, Hibernate, Jakarta Validation and H2. The interface uses HTML, CSS and plain JavaScript. Tests use JUnit 5, MockMvc and AssertJ.

The project includes Maven Wrapper configured for Maven 4.0.0-rc-4, a preview release of Maven 4. A separate Maven installation is not required.

Running locally

Install JDK 21 and open a terminal in the project directory. Internet access is required for the first dependency download.

Run this command:

    ./mvnw spring-boot:run

Open http://localhost:8080 in your browser. Stop the server with Ctrl+C.

If port 8080 is occupied, stop the previous application instance or use another port:

    PORT=8088 ./mvnw spring-boot:run

Then open http://localhost:8088. Localhost addresses work only while the server is running on your computer.

Using IntelliJ IDEA

Open pom.xml as a Maven project, select JDK 21 and wait for Maven to load the dependencies. Run JobBoardApplication.

If the IDE reports Java 5 or source version 1.5, check that the module language level and compiler target are 21, select Maven Wrapper and reload the Maven project.

Run only one instance against the local H2 database at a time. A second instance can fail because the database file is already in use.

Building and testing

Run the tests:

    ./mvnw test

Build and verify the application:

    ./mvnw clean verify

Run the resulting JAR from the project directory:

    java -jar target/job-board-1.0.0.jar

Seven integration tests cover CRUD operations, search, validation, rejected updates, missing records, malformed requests and HTML delivery. They use a separate in-memory H2 database and do not modify saved local vacancies. Browser interactions are checked manually.

How the application works

JobBoardApplication starts Spring Boot and the embedded web server. JobController handles HTTP requests. JobRequest defines the input fields and validation rules. JobService coordinates operations and transaction boundaries. JobRepository accesses the database through Spring Data JPA. Job maps vacancy data to the jobs table. ApiExceptionHandler formats validation and missing-record errors.

The static interface is in src/main/resources/static. JavaScript sends requests to the API and displays the results. Application settings are in src/main/resources/application.properties. Integration tests are in JobApiTests.java.

API

GET /api/jobs lists vacancies, highest ID first.
GET /api/jobs?q=java searches titles and companies.
GET /api/jobs/{id} retrieves one vacancy.
POST /api/jobs creates a vacancy.
PUT /api/jobs/{id} replaces all editable fields.
DELETE /api/jobs/{id} deletes a vacancy.

POST and PUT accept JSON with title, company, location, description and experienceYears. The database generates the ID. Validation errors include field-specific messages in an errors object.

Data storage

Vacancies are saved in the data directory relative to the application's working directory. They remain after a restart. Run from the same directory to keep using the same database. Local data, build output and IntelliJ settings are excluded from Git.

To reset the database, stop the application and remove the data directory. This deletes all saved vacancies.

Project scope

This is a local learning application without accounts or access control. It listens on 127.0.0.1. Possible improvements include pagination, location filtering, vacancy statuses, PostgreSQL, database migrations and automated browser tests.

Documentation

A detailed Russian code walkthrough is available in docs/Job-Board-Code-Guide-RU.pdf.

Project background

The idea was inspired by studying the Telusko Spring course. This application was implemented separately without copying course source files. Spring Initializr provided the standard starter and Maven Wrapper. Implementation and documentation were created with Codex assistance.

Original course: https://github.com/navinreddy20/spring6-course

Author

Yehor Zinchenko. Learning Java backend development and data engineering.
GitHub: https://github.com/YehorZinchenko
=======
# java-job-board
Java-spring project
>>>>>>> aff388b5f38a84858d3bfe16748daff2f673e089
