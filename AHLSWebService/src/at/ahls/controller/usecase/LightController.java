package at.ahls.controller.usecase;

import at.ahls.light.LightState;
import at.ahls.web.http.HTTPMethod;
import at.ahls.web.http.HTTPRequest;
import at.ahls.web.http.exception.UnsuccessfulRequestException;

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

}
