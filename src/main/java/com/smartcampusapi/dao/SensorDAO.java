/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.dao;

/**
 *
 * @author User
 */
import com.smartcampusapi.model.Sensor;
import com.smartcampusapi.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SensorDAO {

	private static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
	private static final Map<String, List<SensorReading>> READINGS = new ConcurrentHashMap<>();

	private SensorDAO() {
	}

	public static List<Sensor> getAllSensors() {
		return new ArrayList<>(SENSORS.values());
	}

	public static List<Sensor> getSensorsByType(String type) {
		return SENSORS.values().stream()
				.filter(sensor -> sensor.getType() != null && sensor.getType().equalsIgnoreCase(type))
				.collect(Collectors.toList());
	}

	public static Sensor getSensor(String id) {
		return SENSORS.get(id);
	}

	public static Sensor createSensor(Sensor sensor) {
		SENSORS.put(sensor.getId(), sensor);
		READINGS.putIfAbsent(sensor.getId(), new CopyOnWriteArrayList<>());
		return sensor;
	}

	public static Sensor deleteSensor(String id) {
		READINGS.remove(id);
		return SENSORS.remove(id);
	}

	public static List<SensorReading> getReadingsForSensor(String sensorId) {
		return READINGS.getOrDefault(sensorId, new ArrayList<>());
	}

	public static SensorReading addReading(String sensorId, SensorReading reading) {
		if (reading.getId() == null || reading.getId().isBlank()) {
			reading.setId(UUID.randomUUID().toString());
		}
		if (reading.getTimestamp() <= 0) {
			reading.setTimestamp(System.currentTimeMillis());
		}

		READINGS.putIfAbsent(sensorId, new CopyOnWriteArrayList<>());
		READINGS.get(sensorId).add(reading);

		Sensor sensor = SENSORS.get(sensorId);
		if (sensor != null) {
			sensor.setCurrentValue(reading.getValue());
		}

		return reading;
	}
}
