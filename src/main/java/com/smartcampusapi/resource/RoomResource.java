/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.resource;

/**
 *
 * @author User
 */
import com.smartcampusapi.dao.RoomDAO;
import com.smartcampusapi.exception.RoomNotEmptyException;
import com.smartcampusapi.model.Room;
import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

	@GET
	public List<Room> getRooms() {
		return RoomDAO.getAllRooms();
	}

	@POST
	public Response createRoom(Room room, @Context UriInfo uriInfo) {
		if (room == null || room.getId() == null || room.getId().isBlank()) {
			throw new WebApplicationException("Room id is required", Response.Status.BAD_REQUEST);
		}

		RoomDAO.createRoom(room);
		URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
		return Response.created(location).entity(room).build();
	}

	@GET
	@Path("/{roomId}")
	public Room getRoom(@PathParam("roomId") String roomId) {
		Room room = RoomDAO.getRoom(roomId);
		if (room == null) {
			throw new WebApplicationException("Room not found", Response.Status.NOT_FOUND);
		}
		return room;
	}

	@DELETE
	@Path("/{roomId}")
	public Response deleteRoom(@PathParam("roomId") String roomId) {
		Room room = RoomDAO.getRoom(roomId);
		if (room == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		if (!room.getSensorIds().isEmpty()) {
			throw new RoomNotEmptyException("Room " + roomId + " still has active sensors");
		}

		RoomDAO.deleteRoom(roomId);
		return Response.noContent().build();
	}
}
