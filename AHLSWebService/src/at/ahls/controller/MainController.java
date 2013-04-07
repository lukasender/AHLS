package at.ahls.controller;

import at.ahls.controller.usecase.ActivityLogController;
import at.ahls.controller.usecase.LightController;
import at.ahls.controller.usecase.UserController;
import at.ahls.database.DBConnectionController;

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
	
	public DBConnectionController getDBConnectorController() {
		return DBConnectionController.getInstance();
	}
	
	public ActivityLogController getActivityLogController() {
		return ActivityLogController.getInstance();
	}
	
	public UserController getUserController() {
		return UserController.getInstance();
	}

}
