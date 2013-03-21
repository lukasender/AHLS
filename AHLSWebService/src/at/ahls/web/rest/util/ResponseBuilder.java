package at.ahls.web.rest.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ResponseBuilder {

	public static Response ok() {
		return Response.ok().build();
	}
	
	public static Response badRequeset() {
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	public static Response notFound() {
		return Response.status(Status.NOT_FOUND).build();
	}
	
}
