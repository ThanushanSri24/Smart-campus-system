package com.smartcampus.mappers;

import com.smartcampus.exception.SensorUnavilableException;
import com.smartcampus.model.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavilableException> {

    @Override
    public Response toResponse(SensorUnavilableException exception) {
        ErrorResponse error = new ErrorResponse(403, "Forbidden", exception.getMessage());
        return Response.status(Response.Status.FORBIDDEN).entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}
