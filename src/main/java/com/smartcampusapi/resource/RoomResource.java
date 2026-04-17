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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.smartcampusapi.dao.RoomDAO;
import com.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.smartcampusapi.exception.RoomNotEmptyException;
import com.smartcampusapi.model.Room;

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
			throw new WebApplicationException("Room id is required in the request body. Please try again", Response.Status.BAD_REQUEST);
		}
		// Checking for duplicate id for rooms 
		if (RoomDAO.getRoom(room.getId()) != null) {
			throw new WebApplicationException("A room with this id already exists. Please try again", Response.Status.CONFLICT);
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
			throw new LinkedResourceNotFoundException("Room with id '" + roomId + "' was not found.");
		}
		return room;
	}

	@DELETE
	@Path("/{roomId}")
	public Response deleteRoom(@PathParam("roomId") String roomId) {
		Room room = RoomDAO.getRoom(roomId);
		if (room == null) {
			throw new LinkedResourceNotFoundException("Room with id '" + roomId + "' was not found.");
		}
		if (!room.getSensorIds().isEmpty()) {
			throw new RoomNotEmptyException("Room " + roomId + " still has usable active sensors");
		}
		RoomDAO.deleteRoom(roomId);
		return Response.noContent().build();
	}
}
