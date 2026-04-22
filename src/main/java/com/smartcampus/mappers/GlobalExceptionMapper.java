package com.smartcampus.mappers;

import com.smartcampus.model.ErrorResponse;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // Log full stack trace server-side for diagnostics — never sent to client
        LOGGER.log(Level.SEVERE, "Unexpected error caught by global handler: " + exception.getMessage(), exception);

        ErrorResponse error = new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred. Please contact the system administrator.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}
