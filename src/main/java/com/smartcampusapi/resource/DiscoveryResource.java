/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.resource;

/**
 *
 * @author User
 */
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

	@GET
	public Map<String, Object> discover() {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("service", "Smart Campus API");
		response.put("version", "v1");
		response.put("contact", "campus-it@westminster.ac.uk");

		Map<String, String> resources = new LinkedHashMap<>();
		resources.put("rooms", "/api/v1/rooms");
		resources.put("sensors", "/api/v1/sensors");
		response.put("resources", resources);

		return response;
	}
}
