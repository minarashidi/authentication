# Authentication Service

Authentication service is a Spring Boot application to manage user registration and authentication.
Expose APIs for user registration , authenticating registered users and to retrieve the 10 most recent login attempts for a user.

---

### Technologies / Changes

* Java 21
* Spring Boot 3.2(with virtual threads)
* Maven
* Use docker-compose
* TestContainers for Integration Tests
* JDBC Client
* Swagger API documentation
* TODO: Monitoring and Observability using Spring Boot Actuator and Micrometer

---

### How to Build

- Navigate to the root directory and run: `./mvn clean install`

### How to Run

1. Change to `/scripts`
2. run `./start-services.sh`
3. Run `./create-database.sh`
4. Launch the application using: `mvn spring-boot:run`
5. To interact with the database, you can run:
    - `docker ps` to obtain the Container ID for the postgres image, then execute:
    - `docker exec -it ${containerId} psql authentication -U postgres`
    - While in the container, run `\dt authentication.` to view the list of created tables.
   ```sql 
   select * from authentication.user;
   select * from authentication.login_attempt; 
   ```

### API Documentation

Swagger: Access the Swagger API documentation
at: [http://localhost:8080/authentication-docs/swagger-ui/index.html](http://localhost:8080/authentication-docs/swagger-ui/index.html) when
the application is running.

(Note: to test `loginAttempts` endpoint, grab the returned JWT token from the `login` endpoint, then just click on the `Authorize` button and paste the token)

[Postman Collection](authentication.postman_collection.json)

[Sequence diagram](authentication-sequence-diagram.png)

---

### Design Decisions

**Backend Service**
I have used Spring Boot 3.2(Spring MVC), which was released recently and added support for Virtual Threads on JDK 21.
(To use Virtual Threads, I just set the property `spring.threads.virtual.enabled` to `true`.)

Our Tomcat will use virtual threads for HTTP requests, means our application runs on virtual threads to achieve high throughput.

**JDBC Client** is used, since Spring Framework 6.1 introduced JDBC Client that gives us a fluent API for talking to a database.

**API Clients**
* Integration tests to call the authentication APIs.
* Swagger is used to call the endpoints.
* Postman collection are also included.

**Service(Client) to service(backend) communication**
There are two ways to call backend APIs:

* Spring MVC app(Servlet stack)
  * old way: Rest template(feature complete; a lot of overloaded methods(confusing) for making service call)
  * new way: Rest Client(Fluent API)

* Spring Webflux(Reactive stack)
  Web Client(Fluent API)

In a real-world application, we might need to have a separate (frontend) application to interact with backend service.


Nice sum up, I'm definitely with JDBC client; because of its Simplicity and no magic behind It's better readable and maintable over others and 
full control of DB queries, no magic even for crud operations 
---

### The Need for Monitoring and Observability

We need to establish robust monitoring and observability solutions.
By implementing these, we can gain insights into the performance and behavior of our application.

#### Metrics

I recommend to set up Prometheus as the monitoring backend and Grafana for creating informative dashboards to visualize and analyze data.
Metrics should be exposed through HTTP, and Prometheus can be configured to scrape data at regular intervals from the /prometheus endpoint.

We can take advantage of Spring Boot production-ready features that are packed in a module
called actuator https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html.

#### Tracing

We can use micrometer-tracing, which provides a simple facade for the most popular tracer libraries, and letting us instrument our
application code without vendor lock-in.
https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.micrometer-tracing
