/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampusapi.resource;

/**
 *
 * @author User
 */

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.smartcampusapi.dao.RoomDAO;
import com.smartcampusapi.dao.SensorDAO;
import com.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.smartcampusapi.model.Room;
import com.smartcampusapi.model.Sensor;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        if (type == null || type.isBlank()) {
            return SensorDAO.getAllSensors();
        }
        return SensorDAO.getSensorsByType(type);
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            throw new WebApplicationException("Sensor id is required in the request body.", Response.Status.BAD_REQUEST);
        }
        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            throw new WebApplicationException("roomId is required in the request body.", Response.Status.BAD_REQUEST);
        }

        Room room = RoomDAO.getRoom(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room with id '" + sensor.getRoomId() + "' does not exist. Cannot assign sensor.");
        }

        if (sensor.getStatus() == null || sensor.getStatus().isBlank()) {
            sensor.setStatus("ACTIVE");
        }

        SensorDAO.createSensor(sensor);
        room.getSensorIds().add(sensor.getId());

        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Sensor getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = SensorDAO.getSensor(sensorId);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor with id '" + sensorId + "' not found.");
        }
        return sensor;
    }

    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor deleted = SensorDAO.deleteSensor(sensorId);
        if (deleted == null) {
            throw new LinkedResourceNotFoundException("Sensor with id '" + sensorId + "' not found. Cannot delete.");
        }
        Room room = RoomDAO.getRoom(deleted.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }
        return Response.noContent().build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
