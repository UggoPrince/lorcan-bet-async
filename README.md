# Inventory and Order Management System with Asynchronous Processing and Distributed Consistency.

This service is set up with Java and SpringBoot framework. This is so because java is very good with concurrency and threading. Which 
makes it suitable for what is expected from the service.

To run the service on your computer, clone the repository, and change these values in the application.properties file
 to your own.

The system runs kafka, so you need to have kafka installed and setup before starting the service. Alternatively, you can use a 
remote kafka service.
Also, for the migration, the flyway dependency handles that for you. Only provide your Postgresql database credentials.

After that, execute the following maven commands on your cmd:

- **_mvn install_** to install dependencies and build project
- **_mvn spring-boot:run_** to run the server

This can easily be done with IntelliJ IDE.

Visit the swagger documentation on **http://localhost:port/swagger-ui/index.html#/**
to get all the available endpoints.
