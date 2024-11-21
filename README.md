# Knight 2 Meet You

“Knight 2 Meet You” envisions a seamless but enjoyable way to manage chess tournaments. In addition to the basic features (tournament CRUD & matchmaking, player registration etc), we have included additional novel features such as a Chess Chatbot, a Check-In system and an enhanced Password Security setup for account protection.

We have created a FrontEnd website to display and demonstrate various features as part of this RESTful API.
[FrontEnd Repository Link](https://github.com/shiv-iyer/CS203-Project)

## Features and Design
- User Interface design: we are creating a FrontEnd website to display the basic information necessary for our application (Tournaments, User information). It is built using React JS.
- Database design: we have implemented persistency and created a database with entities. Included are GET, POST, and PUT operations for our relevant entities: Player, Matches, and Tournament.
- General design principles: Separation of Concerns is achieved between controllers, services, repositories and entities, resulting in each handling and managing their own responsibilities, such as controllers handling HTTP requests and repositories handling data persistence.
- Security features: we have implemented more advanced security methods to protect our application. Instead of using HTTP base64 encoding — a relatively weak means of security — we did salting and hashing of passwords in the database, adding an extra layer of security.
- RESTful API Implementation

## Others

### To run the spring project on windows, use : mvnw.cmd spring-boot:run OR .\mvnw spring-boot:run

### To get to the h2 database console, run the spring boot and type this in your browser : http://localhost:8080/h2-console

### JDBC URL : jdbc:h2:file:./data/testdb , username : username ,  password = password

### To run the test, use : mvn verify

### To see our Knight2MeetYouAPI, type this in your browser : http://localhost:8080/swagger-ui/index.html