# Knight 2 Meet You

“Knight 2 Meet You” envisions a seamless but enjoyable way to manage chess tournaments. In addition to the basic features (tournament CRUD & matchmaking, player registration etc), we have included additional novel features such as a Chess Chatbot, a Check-In system and an enhanced Password Security setup for account protection.

## Features and Design
- User Interface design: we are creating a FrontEnd website to display the basic information necessary for our application (Tournaments, User information). It is built using React JS.
- Database design: we have implemented persistency and created a database with entities. Included are GET, POST, and PUT operations for our relevant entities: Player, Matches, and Tournament.
- General design principles: Separation of Concerns is achieved between controllers, services, repositories and entities, resulting in each handling and managing their own responsibilities, such as controllers handling HTTP requests and repositories handling data persistence. Security features have also been implemented, such as password hashing in the database through bcrypt.
- RESTful API Implementation

### To run the spring project on windows, use : mvnw.cmd spring-boot:run OR .\mvnw spring-boot:run

### To get to the h2 database console, run the spring boot and type this in your browser : http://localhost:8080/h2-console

#### JDBC URL : jdbc:h2:mem:testdb , username : username ,  password = password
