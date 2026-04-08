package com.smartcampusapi.resource;

import com.smartcampusapi.dao.SensorDAO;
import com.smartcampusapi.exception.SensorUnavailableException;
import com.smartcampusapi.model.Sensor;
import com.smartcampusapi.model.SensorReading;
import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        Sensor sensor = SensorDAO.getSensor(sensorId);
        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }
        return SensorDAO.getReadingsForSensor(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        Sensor sensor = SensorDAO.getSensor(sensorId);
        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is in MAINTENANCE mode");
        }

        SensorReading saved = SensorDAO.addReading(sensorId, reading);
        URI location = uriInfo.getAbsolutePathBuilder().path(saved.getId()).build();
        return Response.created(location).entity(saved).build();
    }
}
