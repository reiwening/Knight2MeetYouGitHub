# Knight 2 Meet You

“Knight 2 Meet You” envisions a seamless but enjoyable way to manage chess tournaments. In addition to the basic features (tournament CRUD & matchmaking, player registration etc), we have included additional novel features such as a Chess Chatbot, a Check-In system and an enhanced Password Security setup for account protection.

## Features and Design
- User Interface design: we are creating a FrontEnd website to display the basic information necessary for our application (Tournaments, User information). It is built using React JS.
- Database design: we have implemented persistency and created a database with entities. Included are GET, POST, and PUT operations for our relevant entities: Player, Matches, and Tournament.
- General design principles: Separation of Concerns is achieved between controllers, services, repositories and entities, resulting in each handling and managing their own responsibilities, such as controllers handling HTTP requests and repositories handling data persistence.
- Security features: we have implemented more advanced security methods to protect our application. Instead of using HTTP base64 encoding — a relatively weak means of security — we did salting and hashing of passwords in the database, adding an extra layer of security.
- RESTful API Implementation

## Others

### To run the spring project on windows, use : mvnw.cmd spring-boot:run OR .\mvnw spring-boot:run
### To run test in windows terminal, use: mvn test

### To get SQL database, do the following
1. Run WAMP
2. Open SQL workbench
3. Create a new connection (Right of "My SQL Connections")
4. Set connection name to k2mu
5. Press ok
6. Click into it
7. If prompted for a password, put in password1!
8. Create new schema called k2mu and set it as the default schema
9. Run with .\mvnw spring-boot:run
10. Refresh schema and check that it has been populated with tables

### To get to the h2 database console, run the spring boot and type this in your browser : http://localhost:8080/h2-console

#### JDBC URL : jdbc:h2:file:./data/testdb , username : username ,  password = password

### To run the test files, use : mvn clean test
mvnw test -Dtest=MatchServiceTest,
mvnw test -Dtest=SpringBootIntegrationTest

### To view jacoco code coverage test report
Enter URL into browser: target/site/jacoco/index.html

### To see our Knight2MeetYouAPI, type this in your browser : http://localhost:8080/swagger-ui/index.html