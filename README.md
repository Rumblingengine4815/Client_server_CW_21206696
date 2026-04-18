# Smart Campus API (5COSC022W Coursework)
Name: Lorion Surjith Ravidradasan
ID: 212066096
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
- src/main/java/com/smartcampusapi/filter (optional)
- pom.xml
- README.md (must include report answers)



## 2. Build and Run

### Option A: NetBeans
1. Open NetBeans.
2. File -> Open Project -> choose SmartCampusAPI folder.
3. Wait for Maven dependencies to load.
4. In Projects panel, expand Source Packages.
5. Right click com.smartcampusapi.config.Main -> Run File.
6. Confirm server starts at http://localhost:8080/api/v1/



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

## Part 1.1: JAX-RS Resource Lifecycle

JAX-RS doesn't treat resource classes as global singletons. That means the resource class itself shouldn't hold application data. Maps and lists need to be thread-safe if multiple requests access them at the same time. In this project, the DAO layer uses concurrent collections so data doesn't get corrupted under concurrent access.

## Part 1.2: Hypermedia

Hypermedia is useful because it gives clients links in the response so they don't have to rely only on static documentation. If the API structure changes, the client can still find related resources through the links. It's a more RESTful way of designing APIs and easier.

## Part 2.1: Returning IDs vs Full Room Objects

Returning only IDs keeps the response smaller, but the client has to make more requests to get the full data. Returning full objects is easier for the client but makes the response bigger. For our coursework, returning full objects for detail views made the most sensible.

## Part 2.2: DELETE Idempotency

DELETE is idempotent because repeating the same request doesn't keep changing the server state. If a room is deleted once, sending the same DELETE again doesn't delete anything new. In this implementation, the first request removes the room, and later requests will fail because it's already gone.

## Part 3.1: @Consumes(MediaType.APPLICATION_JSON)

`@Consumes(MediaType.APPLICATION_JSON)` means the endpoint only takes JSON. If a client sends `text/plain` or `application/xml`, JAX-RS won't match that request, so it returns an unsupported media type error. This keeps the API predictable and safe.

## Part 3.2: QueryParam vs Path Segment for Filtering

Using `?type=CO2` is better for filtering because the main resource stays the same and only the search criteria changes. A path-based version like `/sensors/type/CO2` can work, but it's not as flexible if more filters are added later. Query parameters fit filtering much better.

## Part 4.1: Sub-Resource Locator Benefits

Sub-resource locators keep things clean by splitting nested logic into separate classes. Instead of one huge controller, sensor readings get their own class. That makes the code easier to read, test, and extend later.

## Part 5.2: Why 422 for Missing Linked Resource

422 is better than 404 when the JSON is valid but one of the values inside doesn't point to a real resource. The server understands the request, but it can't process it because the linked resource doesn't exist.

## Part 5.4: Security Risk of Exposing Stack Traces

Stack traces shouldn't be shown to API users because they reveal internal details. An attacker could learn class names, file paths, library versions, and other stuff that makes targeting attacks easier. Generic error messages are much safer.



