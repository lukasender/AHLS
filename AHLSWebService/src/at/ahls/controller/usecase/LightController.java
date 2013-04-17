package at.ahls.controller.usecase;

import at.ahls.light.LightState;
import at.ahls.web.http.HTTPMethod;
import at.ahls.web.http.HTTPRequest;
import at.ahls.web.http.exception.UnsuccessfulRequestException;
import at.ahls.web.rest.api.jaxb.ActivityDto;
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
	
	public LightDataDto prepareDummyLightData() {
		LightDataDto light = new LightDataDto();
		light.setOn(true);
		light.setCt(153);
		light.setBri(255);
		light.setTt(Long.valueOf(50));
		
		return light;
	}

	public LightDataDto reactOnActivityData(ActivityDto activity) {
		
		int data = Integer.valueOf(activity.getData());
		Integer brightness = 50;
		if (data > 600) {
			brightness = 255;
		}
		if (data < 400) {
			brightness = 255;
		}
		
		LightDataDto light = new LightDataDto();
		light.setOn(true);
		light.setCt(153);
		light.setBri(brightness);
		light.setTt(Long.valueOf(50));
		
		
		return light;
	}
}
