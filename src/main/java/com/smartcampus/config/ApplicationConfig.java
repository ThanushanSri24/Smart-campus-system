package com.smartcampus.config;

// Import JAX-RS classes for configuration


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/*
 * This class is used to configure the base path for the REST API.
 * It extends the JAX-RS Application class to activate REST services.
 */
@ApplicationPath("/api/v1") // Defines the base URI for all API endpoints
public class ApplicationConfig extends Application {
    // No additional configuration is required here for basic setup
}
