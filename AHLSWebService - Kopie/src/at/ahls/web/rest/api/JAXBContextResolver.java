//package at.ahls.web.rest.api;
//
//import javax.ws.rs.ext.ContextResolver;
//import javax.ws.rs.ext.Provider;
//import javax.xml.bind.JAXBContext;
//
//import at.ahls.web.rest.api.jaxb.ActivitiesDto;
//import at.ahls.web.rest.api.jaxb.ActivityDto;
//import at.ahls.web.rest.api.jaxb.LightDataDto;
//import at.ahls.web.rest.api.jaxb.LightsDataDto;
//import at.ahls.web.rest.api.jaxb.UserDto;
//
//import com.sun.jersey.api.json.JSONConfiguration;
//import com.sun.jersey.api.json.JSONJAXBContext;
//
//@Provider
//public class JAXBContextResolver implements ContextResolver<JAXBContext> {
//	private JAXBContext context;
//	private Class<?>[] types = {
//			ActivitiesDto.class,
//			ActivityDto.class,
//			LightsDataDto.class,
//			LightDataDto.class,
//			UserDto.class
//		};
//	
//	public JAXBContextResolver() throws Exception {
//		System.out.println("Init JAXBContextResolver");
//		this.context = new JSONJAXBContext(JSONConfiguration.natural().build(), types);
//	}
//
//	public JAXBContext getContext(Class<?> objectType) {
//		System.out.println("JAXBContextResolver: getContext()");
//		for (Class<?> type : types) {
//			if (type == objectType) {
//				return context;
//			}
//		}
//		return null;
//	}
//	
//}
