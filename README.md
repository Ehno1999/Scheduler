Scheduler

SQL Script Scheduler
This project is a SQL script scheduling tool built with Spring Boot. It allows users to schedule SQL queries to be executed at specified times and intervals. The application stores these tasks and schedules them for execution using Spring's task scheduler.

Features
Schedule SQL queries with a specified run time and interval.
View, update, and delete scheduled tasks.
Query tasks and get the result of the SQL query executed.

Technologies Used
Spring Boot - Framework for building the application.
H2 Database - In-memory database used for storing scheduled tasks.
Maven - Build tool for managing dependencies.
HTML/CSS/JavaScript - Frontend for the web interface.
Getting Started
Prerequisites
Java 17
Maven (for building the project)

Installing
Build the project using Maven:
mvn clean install
Run the Spring Boot application:
mvn spring-boot:run
Once the application starts, open your web browser and navigate to:
http://localhost:8080


Configuration
To configure the database connection, make sure to add the following properties to your application.properties file:
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
This configures Spring Boot to use an in-memory H2 database for storing scheduled tasks.

Usage
Schedule a Query:
Enter the SQL query you want to schedule.
Provide a Run Time (in the format YYYY-MM-DD HH:MM:SS).
Set the interval (in days) if the query needs to repeat.
Click the Schedule Query button.
Fetch All Scheduled Queries:

Click the Fetch All Scheduled Queries button to see the list of all tasks that have been scheduled.
Update or Delete a Task:

Each scheduled query has options to Update or Delete.
Update the query, date, or interval and click Update to save the changes.
Click X to delete the task.
API Endpoints

POST http://localhost:8080/tasks/create: Create a new task.

GET http://localhost:8080/tasks/findall: Fetch all scheduled tasks.

PUT http://localhost:8080/tasks/update/{id}: Update a task by ID.

DELETE  http://localhost:8080/tasks/delete/{id}: Delete a task by ID