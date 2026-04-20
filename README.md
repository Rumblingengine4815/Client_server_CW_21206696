# Smart Campus API (5COSC022W Coursework)
Name: Lorion Surjith Ravidradasan
ID: 21206696
IIT ID : 20230198

## 1. API Overview
This project implements a JAX-RS RESTful API for Smart Campus Room and Sensor Management.

Core resources:
- Room
- Sensor
- SensorReading

Base URL:
- http://localhost:8080/api/v1

Main collections:
- /rooms
- /sensors
- /sensors/{sensorId}/readings

This coursework includes:
- Resource-based endpoint design
- Query filtering (sensor type)
- Sub-resource locator for readings
- Custom exception classes and exception mappers
- Global safety-net mapper for unexpected errors in case.

## 1.1 Folder Structure 

- src/main/java/com/smartcampusapi/config
- src/main/java/com/smartcampusapi/dao
- src/main/java/com/smartcampusapi/model
- src/main/java/com/smartcampusapi/resource
- src/main/java/com/smartcampusapi/exception
- src/main/java/com/smartcampusapi/filter 
- pom.xml
- README.md 



## 2. Build and Run

### Instructions
1. Open NetBeans. Do this "mvn clean package" if needed as not to run any other file
2. File -> Open Project -> choose SmartCampusAPI folder.
3. Wait for Maven dependencies to load.
4. In Projects panel, expand Source Packages.
5. Right click com.smartcampusapi.config.Main -> Run File.
6. Confirm server starts at http://localhost:8080/api/v1/

Sample curl Commands

### Create a Room


curl -X POST http://localhost:8080/api/v1/rooms \  -H "Content-Type: application/json" \  -d '{"id":"R1","name":"Lecture Theatre 1","capacity":120}'


### Create a Sensor


curl -X POST http://localhost:8080/api/v1/sensors \  -H "Content-Type: application/json" \  -d '{"id":"S1","type":"CO2","roomId":"R1","status":"ACTIVE","currentValue":0.0}'


### Add a Sensor Reading


curl -X POST http://localhost:8080/api/v1/sensors/S1/readings \  -H "Content-Type: application/json" \  -d '{"id":"SR1","value":23.5}'

### List All Rooms

curl http://localhost:8080/api/v1/rooms

### List All Sensors

curl http://localhost:8080/api/v1/sensors

### Delete Sensor

curl -X DELETE http://localhost:8080/api/v1/sensors/S1

### Delete Room

curl -X DELETE http://localhost:8080/api/v1/rooms/R1


## 3.1 Postman Testing

Creating a Postman collection named SmartCampusAPI with this request order:

1. GET {{baseUrl}}/
2. POST {{baseUrl}}/rooms
3. GET {{baseUrl}}/rooms
4. POST {{baseUrl}}/sensors
5. GET {{baseUrl}}/sensors?type=CO2
6. POST {{baseUrl}}/sensors/CO2-001/readings
7. GET {{baseUrl}}/sensors/CO2-001/readings
8. DELETE {{baseUrl}}/rooms/LIB-301 (should fail with 409)
9. POST {{baseUrl}}/sensors with invalid roomId (should fail with 422)
10. POST {{baseUrl}}/sensors/CO2-001/readings after changing status to MAINTENANCE (should fail with 403)

Postman environment variable:
- baseUrl = http://localhost:8080/api/v1

## 4 Report Questions

Smart Campus API - Technical Report (5COSC022W)

### Part 1.1: Lifecycle of JAX-RS resources

A JAX-RS implementation typically instantiates resource classes per request rather than registering them as a singleton in the Application scope. Therefore, resource classes have to be thread-safe: data mutable on the resource itself would cause race conditions. Any state shared between requests has to be stored in distinct components such as DAOs or Services which were explicitly made concurrent. The data-access layer uses concurrent data-structures, preserving data-consistency across concurrent requests without the need for complex request handling logic.

### Part 1.2: Hypermedia (HATEOAS)

Adding hypermedia controls to a response enables API consumers to programmatically discover which actions are available, reducing the need for external documentation. This pattern is commonly known as HATEOAS and improves coupling between server and client. Any change of the API might not necessarily have to be reflected in the client if clients were following HATEOAS links rather than assuming static structure of response. For these reasons, navigation links are included in response when possible and useful, for enabling clients discovering of useful states.

### Part 2.1: Returning identifiers vs. Returning full representations

 Returning identifiers instead of full representation reduces the payload size. The client has then to make another request, and thus costs bandwidth and network round-trips. This is appropriate when the client does not necessarily require full data or when bandwidth is a constraint. Alternatively, returning the entire object graph provides more convenience and potentially better coupling with client code. The choice is to return the full Room representation in detail views as the case-study prioritized simplicity of development and testability. A hybrid model would be preferable in production.

### Part 2.2: Idempotency of DELETE

 The HTTP DELETE method should be idempotent. It is defined that, after an initial successful request, subsequent requests should not change the resource further. A DELETE call once made removes the resource. Further DELETE calls should simply report that the resource has already been deleted, ideally returning 404 Not Found if appropriate or 204 No Content if the response semantics are to communicate success. This project implements the ideal behavior; the first call to DELETE will indeed delete the resource, while subsequent calls return a 404 error, to notify that the requested resource is no longer available.

### Part 3.1: @Consumes(MediaType.APPLICATION_JSON) and Content Negotiation

 A method annotated with @Consumes(MediaType.APPLICATION_JSON) tells the JAX-RS runtime that the method can only accept requests with a Content-Type of application/json. If a client attempts to invoke such method using another Content-Type, JAX-RS would not be able to route the request to that method. The runtime either tries to find an alternative method that can satisfy the request or returns a 415 Unsupported Media Type error. This helps enforce that the request body is parsable and adheres to the schema used by the application.

### Part 3.2: Query Parameters vs. Path Segments for Filtering

 Query parameters are the most natural choice for filtering collections. When asking for a subset of resources that are already represented by another resource, a client is essentially querying an existing resource collection. E.g., GET /sensors?type=CO2&limit=50. The latter alternative of path segments would more accurately identify a fixed sub-resource, such as /rooms/{id}/sensors. This query-parameter approach will remain more extendable than a path-segment based one if additional filters needed.

### Part 4.1: Benefits of sub-resource locators

 Sub-resource locators help to manage and break down resources into their constituents without making the main resource class cumbersome. It is desirable to isolate logical components of resource representations into dedicated classes to improve unit testability and manageability. Unit tests can target specific sub-resources in isolation and a clear distinction of what is the role of each handler becomes apparent with sub-resource locators. This project follows this design with dedicated resource methods for retrieving sensor readings.

### Part 5.2: 422 Unprocessable Entity for faulty links

 A 422 status indicates that the server understands the content of the request body, but it could not process the contained instructions due to semantic errors within the entity, e.g. When a resource cannot be created because its required referenced resources do not exist. A 404 Not Found status indicates that the target resource cannot be found, or endpoint that the client asked for does not exist. This project chooses to return 422 as the body of the request could technically be processed.

### Part 5.4: Risk of exposing stack traces in a response

 Full stack traces provide very detailed information about the server implementation which could be very helpful to a potential attacker to craft malicious requests targeting your API. Stack traces must only be exposed in development environment, not in production; preferably as logs that only authorized persons have access to. Including a transaction-id in the response error will help technical support to locate full stack trace in logs.
