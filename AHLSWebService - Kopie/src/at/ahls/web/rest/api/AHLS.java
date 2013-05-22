package at.ahls.web.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import at.ahls.analyzer.DummyClassOfAnalyzer;
import at.ahls.controller.MainController;
import at.ahls.controller.usecase.ActivityLogController;
import at.ahls.controller.usecase.UserController;
import at.ahls.web.rest.api.jaxb.ActivitiesDto;
import at.ahls.web.rest.api.jaxb.ActivityDto;
import at.ahls.web.rest.api.jaxb.LightDataDto;
import at.ahls.web.rest.api.jaxb.LightsDataDto;
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
	@Produces({MediaType.APPLICATION_JSON})
	public Response testConnection() {		
		System.out.println("Test successful? " + MainController.getInstance().getDBConnectorController().testConnection());
//		ObjectFactory of = new ObjectFactory();
//		ActivityDto dto = new ActivityDto();
//		dto.setData("1338");
//		
//		JAXBElement<ActivityDto> entity = of.createActivity(dto);
//		
//		return Response.ok().entity(new GenericEntity<JAXBElement<ActivityDto>>(entity) {}).build();
		return ResponseBuilder.ok();
	}
	
	
	@GET @Path("/amanntest")
	@Produces({MediaType.APPLICATION_JSON})
	public Response amanntestConnection() {	
		DummyClassOfAnalyzer dsf= new DummyClassOfAnalyzer();
		//dsf.isActive();
		System.out.println("Test successful? " + dsf.getDiff());
//		ObjectFactory of = new ObjectFactory();
//		ActivityDto dto = new ActivityDto();
//		dto.setData("1338");
//		
//		JAXBElement<ActivityDto> entity = of.createActivity(dto);
//		
//		return Response.ok().entity(new GenericEntity<JAXBElement<ActivityDto>>(entity) {}).build();
		return ResponseBuilder.ok();
	}
	
	@POST @Path("/test")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response testConnection(JAXBElement<ActivityDto> activity) {
		System.out.println("AHLS - testConnectino(): ok... something came right here. Let me analyze it quickly.");
		
		ActivityDto activityDto = activity.getValue();
		
		// dummy data
		LightDataDto lightDataDto = MainController.getInstance().getLightController().reactOnActivityData(activityDto);
		
		// create entity
		ObjectFactory of = new ObjectFactory();
		JAXBElement<LightDataDto> entity = of.createLightData(lightDataDto);
				
		// return response and data
		return Response.ok().entity(new GenericEntity<JAXBElement<LightDataDto>>(entity) {}).build();
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
	@Produces({MediaType.APPLICATION_JSON})
	public Response logActivity(JAXBElement<ActivityDto> activity) {
		// fetch from DB
		String data = activity.getValue().getData();
		System.out.println("POST /log: data: " + data);
		if (data == null || data != null && ( "null".equals(data) || data.isEmpty())) {
			return ResponseBuilder.badRequeset();
		}
		
		ActivityLogController controller = MainController.getInstance().getActivityLogController();
		ActivityDto activityDto = activity.getValue();
		controller.insertActivityLog(activityDto.getSensorId(), activityDto.getUsername(), activityDto.getData());
		
		
		// dummy data
		LightDataDto lightDataDto = MainController.getInstance().getLightController().reactOnActivityData(activityDto);
		
		// create entity
		ObjectFactory of = new ObjectFactory();
		JAXBElement<LightDataDto> entity = of.createLightData(lightDataDto);
				
		// return response and data
		return Response.ok().entity(new GenericEntity<JAXBElement<LightDataDto>>(entity) {}).build();
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
