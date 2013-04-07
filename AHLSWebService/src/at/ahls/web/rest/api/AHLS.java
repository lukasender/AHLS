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

import at.ahls.controller.MainController;
import at.ahls.controller.usecase.ActivityLogController;
import at.ahls.controller.usecase.UserController;
import at.ahls.database.DBConnectionController;
import at.ahls.test.jaxb.LightStateType;
import at.ahls.web.rest.api.jaxb.ActivitiesDto;
import at.ahls.web.rest.api.jaxb.ActivityDto;
import at.ahls.web.rest.api.jaxb.ObjectFactory;
import at.ahls.web.rest.api.jaxb.UserDto;
import at.ahls.web.rest.util.ResponseBuilder;

@Path("/ahls")
public class AHLS {

	@GET @Path("/hello")
	public Response sayHello() {
		return ResponseBuilder.ok();
	}
	
	@GET @Path("/test")
	public Response testConnection() {		
		System.out.println("Test successful? " + DBConnectionController.getInstance().testConnection());
		
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
		ActivityLogController controller = MainController.getInstance().getActivityLogController();
		ActivitiesDto activities = controller.getActivities(count);
		
		// create entity
		ObjectFactory of = new ObjectFactory();
		JAXBElement<ActivitiesDto> entity = of.createActivities(activities);
		
		// return response and data
		return Response.ok().entity(new GenericEntity<JAXBElement<ActivitiesDto>>(entity) {}).build();
	}
	
	@POST @Path("/log")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response logActivity(JAXBElement<ActivityDto> activity) {
		// fetch from DB
		String data = activity.getValue().getData();
		System.out.println("POST /log: getData: " + activity.getValue().getData());
		System.out.println("POST /log: data: " + data);
		if (data == null || data != null && ( "null".equals(data) || data.isEmpty())) {
			return ResponseBuilder.badRequeset();
		}
		
		ActivityLogController controller = MainController.getInstance().getActivityLogController();
		controller.insertActivityLog(activity.getValue().getData());
		
		return ResponseBuilder.ok();
	}
	
	@POST @Path("/user")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createUser() {
		System.out.println("POST /user");
		UserController controller = MainController.getInstance().getUserController();
		
		// create new user
		String username = controller.createNewUser();
		System.out.println("POST /user: new user name: " + username);
		UserDto user = new UserDto();
		user.setUsername(username);
		
		// create entity
		ObjectFactory of = new ObjectFactory();
		JAXBElement<UserDto> entity = of.createUser(user);
				
		// return response and data	
		return Response.ok().entity(new GenericEntity<JAXBElement<UserDto>>(entity) {}).build();
	}
	
}
