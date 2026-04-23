package com.smartcampus;

import com.smartcampus.filters.LoggingFilter;
import com.smartcampus.mappers.GlobalExceptionMapper;
import com.smartcampus.mappers.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.mappers.RoomNotEmptyExceptionMapper;
import com.smartcampus.mappers.SensorUnavailableExceptionMapper;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;





public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        // Resource endpoints
        register(DiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);

        // Exception mappers (Part 5)
        register(RoomNotEmptyExceptionMapper.class);
        register(LinkedResourceNotFoundExceptionMapper.class);
        register(SensorUnavailableExceptionMapper.class);
        register(GlobalExceptionMapper.class);

        // Request/response logging filter (Part 5.5)
        register(LoggingFilter.class);

        // Jackson JSON marshalling
        register(JacksonFeature.class);
    }
}
