package com.smartcampus.resource;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)

public class DiscoveryResource {
    @GET
    public Response discover (){
        Map<String, Object> discovery = new HashMap<>();

        // API versioning info
        discovery.put("api", "Smart Campus Sensor & Room Management API");
        discovery.put("version", "1.0.0");
        discovery.put("description", "RESTful API for managing campus rooms and IoT sensors.");

        // Admin contact
        Map<String, String> contact = new HashMap<>();
        contact.put("name", "Campus Facilities Admin");
        contact.put("email", "admin@smartcampus.ac.uk");
        discovery.put("contact", contact);

        // HATEOAS-style resource links
        Map<String, String> links = new HashMap<>();
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        discovery.put("resources", links);

        discovery.put("timestamp", System.currentTimeMillis());

        return Response.ok(discovery).build();
    }


}
