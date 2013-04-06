package at.ahls.web.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import at.ahls.database.ActivityLog;
import at.ahls.database.DBConnector;
import at.ahls.light.controller.LightController;
import at.ahls.light.data.LightState;
import at.ahls.test.jaxb.LightStateType;
import at.ahls.web.rest.api.jaxb.ActivitiesType;
import at.ahls.web.rest.api.jaxb.ActivityType;
import at.ahls.web.rest.api.jaxb.ObjectFactory;
import at.ahls.web.rest.util.ResponseBuilder;

@Path("/ahls")
public class AHLS {

	@GET @Path("/hello")
	public Response sayHello() {
		return ResponseBuilder.ok();
	}
	
	@GET @Path("/test")
	public Response testConnection() {		
		System.out.println("Test successful? " + DBConnector.getInstance().testConnection());
		
		return ResponseBuilder.ok();
	}
	
	@PUT @Path("/test")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response testConnection(JAXBElement<LightStateType> lightState) {
		System.out.println("AHLS - testConnectino(): ok... something came right here. Let me analyze it quickly.");
		LightStateType ls = lightState.getValue();
		
		System.out.println(ls.isOn());
		System.out.println(ls.getBri());
		System.out.println(ls.getHue());
		System.out.println(ls.getSat());
		
		return ResponseBuilder.ok();
	}

	@GET @Path("/log/{count}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getActivities(@PathParam("count") int count) {
		// fetch from DB
		ActivitiesType activities = ActivityLog.getInstance().getActivities(count);
		
		// create entity
		ObjectFactory of = new ObjectFactory();
		JAXBElement<ActivitiesType> entity = of.createActivities(activities);
		
		// return response and data
		return Response.ok().entity(new GenericEntity<JAXBElement<ActivitiesType>>(entity) {}).build();
	}
	
	@POST @Path("/log")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response logActivity(JAXBElement<ActivityType> activity) {
		// fetch from DB
		String data = activity.getValue().getData();
		System.out.println("POST /log: getData: " + activity.getValue().getData());
		System.out.println("POST /log: data: " + data);
		if (data == null || data != null && ( "null".equals(data) || data.isEmpty())) {
			return ResponseBuilder.badRequeset();
		}
		
		ActivityLog.getInstance().insertActivityLog(activity.getValue().getData());
		
		return ResponseBuilder.ok();
	}
	
}
