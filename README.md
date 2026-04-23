# Smart Campus Sensor & Room Management API

A RESTful web service built with **JAX-RS (Jersey 2.41)** and an embedded **Jetty 9** server for managing university campus rooms and IoT sensors. All data is stored in-memory using `ConcurrentHashMap` and `ArrayList` — no database required.

---

## API Design Overview

The API follows a versioned, resource-oriented design rooted at `/api/v1`. It models three core entities:

- **Room** — a physical campus space with a unique ID, name, capacity, and a list of assigned sensor IDs.
- **Sensor** — an IoT device (temperature, CO2, occupancy, etc.) linked to a room, with a status (`ACTIVE`, `MAINTENANCE`, `OFFLINE`) and a current reading value.
- **SensorReading** — a timestamped measurement recorded by a sensor, stored as a historical log.

The resource hierarchy mirrors the physical relationship between these entities:

```
GET  /api/v1                          → Discovery / API metadata
GET  /api/v1/rooms                    → List all rooms
POST /api/v1/rooms                    → Create a room
GET  /api/v1/rooms/{roomId}           → Get a specific room
DEL  /api/v1/rooms/{roomId}           → Delete a room (blocked if sensors exist)

GET  /api/v1/sensors                  → List all sensors (supports ?type= filter)
POST /api/v1/sensors                  → Register a sensor
GET  /api/v1/sensors/{sensorId}       → Get a specific sensor
DEL  /api/v1/sensors/{sensorId}       → Delete a sensor

GET  /api/v1/sensors/{sensorId}/readings   → Get reading history for a sensor
POST /api/v1/sensors/{sensorId}/readings   → Append a new reading (updates sensor's currentValue)
```

Error handling is centralised through JAX-RS `ExceptionMapper` providers, ensuring no raw stack traces ever reach the client. All request/response activity is logged via a `ContainerRequestFilter` / `ContainerResponseFilter`.

---

## Project Structure

```
smart-campus-api/
├── pom.xml
└── src/main/java/com/smartcampus/
    ├── Main.java                          # Jetty server bootstrap
    ├── SmartCampusApplication.java        # JAX-RS ResourceConfig (registers all components)
    ├── config/
    │   └── ApplicationConfig.java
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   ├── SensorReading.java
    │   └── ErrorResponse.java
    ├── service/
    │   └── DataStore.java                 # In-memory ConcurrentHashMap store
    ├── resource/
    │   ├── DiscoveryResource.java         # GET /api/v1
    │   ├── RoomResource.java              # /api/v1/rooms
    │   ├── SensorResource.java            # /api/v1/sensors
    │   └── SensorReadingResource.java     # /api/v1/sensors/{id}/readings (sub-resource)
    ├── exception/
    │   ├── RoomNotEmptyException.java
    │   ├── LinkedResourceNotFoundException.java
    │   └── SensorUnavilableException.java
    ├── mappers/
    │   ├── RoomNotEmptyExceptionMapper.java
    │   ├── LinkedResourceNotFoundExceptionMapper.java
    │   ├── SensorUnavailableExceptionMapper.java
    │   └── GlobalExceptionMapper.java
    └── filters/
        └── LoggingFilter.java
```

---

## Prerequisites

- **Java 11** or higher
- **Apache Maven 3.6+**

Verify your setup:

```bash
java -version
mvn -version
```

---

## How to Build and Run

**1. Clone the repository**

```bash
git clone https://github.com/<your-username>/smart-campus-api.git
cd smart-campus-api
```

**2. Build the project**

```bash
mvn clean package
```

This compiles all sources and produces a fat JAR at `target/smart-campus-api-1.0-SNAPSHOT.jar`.

**3. Start the server**

```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

The server starts on **port 8080**. You should see:

```
INFO: Smart Campus API running on port 8080
INFO: Base URL: http://localhost:8080/api/v1
```

The API is now accessible at `http://localhost:8080/api/v1`.

---

## Sample curl Commands

### 1. Discovery — get API metadata

```bash
curl -X GET http://localhost:8080/api/v1
```

Returns API version, contact details, and links to primary resource collections.

---

### 2. Create a Room

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "LIB-301", "name": "Library Quiet Study", "capacity": 40}'
```

Returns `201 Created` with the room object.

---

### 3. Register a Sensor (linked to the room above)

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "roomId": "LIB-301"}'
```

Returns `201 Created`. Also adds `TEMP-001` to `LIB-301`'s `sensorIds` list.

---

### 4. Filter sensors by type

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
```

Returns only sensors whose `type` matches `Temperature` (case-insensitive).

---

### 5. Post a sensor reading (updates currentValue on the sensor)

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 22.5}'
```

Returns `201 Created` with the reading. The sensor's `currentValue` is updated to `22.5`.

---

### 6. Get reading history for a sensor

```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

Returns the full list of readings recorded for `TEMP-001`.

---

### 7. Attempt to delete a room that still has sensors (expect 409)

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

Returns `409 Conflict` — the room cannot be deleted while sensors are still assigned.

---

### 8. Attempt to register a sensor with a non-existent roomId (expect 422)

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-999", "type": "CO2", "roomId": "DOES-NOT-EXIST"}'
```

Returns `422 Unprocessable Entity`.

---

### 9. Attempt to post a reading to a MAINTENANCE sensor (expect 403)

First, update the sensor status (you can POST a new sensor with status MAINTENANCE, or use your existing sensor):

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-002", "type": "CO2", "status": "MAINTENANCE", "roomId": "LIB-301"}'

curl -X POST http://localhost:8080/api/v1/sensors/CO2-002/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 800}'
```

Returns `403 Forbidden`.

---

## HTTP Status Code Reference

| Status | Scenario |
|--------|----------|
| `200 OK` | Successful GET |
| `201 Created` | Successful POST (room, sensor, reading) |
| `204 No Content` | Successful DELETE |
| `400 Bad Request` | Missing required fields |
| `403 Forbidden` | Reading posted to a MAINTENANCE sensor |
| `404 Not Found` | Resource does not exist |
| `409 Conflict` | Deleting a room with active sensors, or duplicate ID |
| `422 Unprocessable Entity` | Sensor references a non-existent roomId |
| `500 Internal Server Error` | Unexpected runtime error (caught by global mapper) |

---

## Technology Stack

- **Java 11**
- **JAX-RS** via **Jersey 2.41** (no Spring Boot)
- **Jetty 9.4** (embedded servlet container)
- **Jackson** (JSON serialisation via `JacksonFeature`)
- **In-memory storage** — `ConcurrentHashMap` for thread safety, `ArrayList` for readings
- **Maven** build system




## report 
`[View Report](Report.pdf)`
