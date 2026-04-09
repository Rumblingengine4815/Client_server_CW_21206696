# Smart Campus API (5COSC022W Coursework)

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

This implementation includes:
- Resource-based endpoint design
- Query filtering (sensor type)
- Sub-resource locator for readings
- Custom exception classes and exception mappers
- Global safety-net mapper for unexpected errors

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

If NetBeans shows Maven/JDK errors, set JDK 17 in:
- Tools -> Java Platforms
- Project Properties -> Libraries -> Java Platform = JDK 17

### Option B: Command line (Maven)
1. Install JDK 17 and Maven.
2. Ensure JAVA_HOME points to your JDK folder (example shown below).
3. Run:
  set JAVA_HOME=C:\Users\User\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot
   mvn clean compile
4. Start server with:
   mvn exec:java

## 3. Sample curl commands (for demo video)

1. Discovery endpoint:
```bash
curl -X GET http://localhost:8080/api/v1/
```

2. Create a room:
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":120}"
```

3. List rooms:
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

4. Create a sensor linked to room:
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"roomId\":\"LIB-301\"}"
```

5. Filter sensors by type:
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

6. Add reading for sensor:
```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":540.7}"
```

7. View sensor reading history:
```bash
curl -X GET http://localhost:8080/api/v1/sensors/CO2-001/readings
```

8. Try deleting room with active sensor (expected 409):
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

## 3.1 Postman Testing (recommended for marking and video)

Create a Postman collection named SmartCampusAPI with this request order:

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

Useful Postman Tests tab snippets:

Status code check:
```javascript
pm.test("Status code is expected", function () {
  pm.response.to.have.status(201);
});
```

JSON field check:
```javascript
pm.test("Response has id", function () {
  const jsonData = pm.response.json();
  pm.expect(jsonData.id).to.exist;
});
```

Error response check (example for 409):
```javascript
pm.test("Room delete blocked", function () {
  pm.response.to.have.status(409);
  const jsonData = pm.response.json();
  pm.expect(jsonData.error).to.eql("Conflict");
});
```

What to show in video from Postman:
- Collection run order
- Request body and response body for success cases
- Three custom error scenarios: 409, 422, 403
- Clear status codes in each response

