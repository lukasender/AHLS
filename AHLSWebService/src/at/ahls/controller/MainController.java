package at.ahls.controller;

import at.ahls.light.controller.LightController;

public class MainController {

	private static MainController _controller;
	
	private MainController() {}
	
	public static MainController getInstance() {
		if (_controller == null) {
			_controller = new MainController();
		}
		
		return _controller;
	}
	
	public LightController getLightController() {
		return LightController.getInstance();
	}

}
