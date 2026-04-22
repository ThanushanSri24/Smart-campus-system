package com.smartcampus.resource;

import com.smartcampus.service.DataStore;
import com.smartcampus.exception.SensorUnavilableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    
    @GET
    public Response getReadings() {
        List<SensorReading> readings = DataStore.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }

    
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensor(sensorId);

        // Part 5.3 - State Constraint: block MAINTENANCE sensors
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavilableException(
                    "Sensor '" + sensorId + "' is currently under MAINTENANCE and cannot accept new readings."
            );
        }

        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody("Reading body is required."))
                    .build();
        }

        // Automatic UUID and timestamp if not provided
        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Persist the reading
        DataStore.addReading(sensorId, reading);

        // Side-effect: update the parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    

    private java.util.Map<String, String> errorBody(String message) {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("error", message);
        return body;
    }
}
