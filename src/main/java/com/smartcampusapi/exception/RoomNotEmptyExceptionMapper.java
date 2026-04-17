package com.smartcampusapi.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ApiError error = new ApiError(409, "Conflict", exception.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
