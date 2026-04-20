package com.smartcampusapi.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
   
        if (exception instanceof WebApplicationException wae) {
            Response exResp = wae.getResponse();
            int status = exResp != null ? exResp.getStatus() : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            String title = Response.Status.fromStatusCode(status).getReasonPhrase();
            String detail = wae.getMessage() != null ? wae.getMessage() : title;
            ApiError error = new ApiError(status, title, detail);
            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        ApiError error = new ApiError(500, "Internal Server Error", "An unexpected error occurred. Please check server logs and try again.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
