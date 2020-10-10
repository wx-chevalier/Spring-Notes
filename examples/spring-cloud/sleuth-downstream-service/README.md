# Log Tracing with Spring Cloud Sleuth
 
This project shows how to implement tracing in a network of Spring Boot applications.

## Companion Blog Article
The companion blog article to this repository can be found [here](https://reflectoring.io/tracing-with-spring-cloud-sleuth/).

## Getting Started
This application is a service facing the user (a "downstream" service), meaning that 
it accesses other upstream services to provide its functionality.  

1. Start the [upstream service](../sleuth-upstream-service/) with `./gradlew bootrun`
1. Start this service with `./gradlew bootrun`
1. Open `http://localhost:8080/customers-with-address/{id}` where IDs from 1-50 are 
   will return a valid HTTP 200 response and other IDs will return an error response.
1. Look into the log files of both services and verify that both contain the same
   trace id.
