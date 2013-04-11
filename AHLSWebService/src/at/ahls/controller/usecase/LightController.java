package at.ahls.controller.usecase;

import at.ahls.light.LightState;
import at.ahls.web.http.HTTPMethod;
import at.ahls.web.http.HTTPRequest;
import at.ahls.web.http.exception.UnsuccessfulRequestException;
import at.ahls.web.rest.api.jaxb.LightDataDto;
import at.ahls.web.rest.api.jaxb.LightsDataDto;

public class LightController {

	private static LightController _controller;
	private String _url;
	
	private LightController() {}
	
	public static LightController getInstance() {
		if (_controller == null) {
			_controller = new LightController();
		}
		
		return _controller;
	}
	
	public void setUrl(String url) {
		_url = url;
	}
	
	public void sendData(int light, LightState state) throws UnsuccessfulRequestException {
		new HTTPRequest(_url, state.toJSON(), HTTPMethod.PUT).connect();
	}
	
	public LightsDataDto prepareLightData(int sensorId) {
		LightsDataDto lightsData = new LightsDataDto();
		
		LightDataDto light1 = new LightDataDto();
		light1.setLightId(1);
		light1.setOn(true);
		light1.setCt(153);
		light1.setBri(255);
		light1.setTransitiontime(Long.valueOf(50));
		
		LightDataDto light2 = new LightDataDto();
		light2.setLightId(2);
		light2.setOn(true);
		light2.setCt(153);
		light2.setBri(255);
		light2.setTransitiontime(Long.valueOf(50));
		
		LightDataDto light3 = new LightDataDto();
		light3.setLightId(3);
		light3.setOn(true);
		light3.setCt(153);
		light3.setBri(255);
		light3.setTransitiontime(Long.valueOf(50));
		
		lightsData.getLightData().add(light1);
		lightsData.getLightData().add(light2);
		lightsData.getLightData().add(light3);
		
		return lightsData;
	}

}
