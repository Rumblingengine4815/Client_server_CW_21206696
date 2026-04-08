/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.dao;

/**
 *
 * @author User
 */
import com.smartcampusapi.model.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomDAO {

	private static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();

	private RoomDAO() {
	}

	public static List<Room> getAllRooms() {
		return new ArrayList<>(ROOMS.values());
	}

	public static Room getRoom(String id) {
		return ROOMS.get(id);
	}

	public static Room createRoom(Room room) {
		ROOMS.put(room.getId(), room);
		return room;
	}

	public static Room deleteRoom(String id) {
		return ROOMS.remove(id);
	}
}
