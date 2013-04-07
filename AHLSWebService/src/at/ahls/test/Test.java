package at.ahls.test;

import at.ahls.controller.MainController;
import at.ahls.controller.usecase.LightController;
import at.ahls.light.LightState;
import at.ahls.web.http.exception.UnsuccessfulRequestException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LightController controller = MainController.getInstance().getLightController();
		controller.setUrl("http://localhost:8080/AHLSWebService/ahls/test");
		
		LightState state0 = new LightState(true, 4000, 200, 200);
		LightState state1 = new LightState(true, 4000, null, 200);
		LightState state2 = new LightState(true, 4000, 200, null);
		LightState state3 = new LightState(true, null, 200, 200);
		
		try {
			controller.sendData(0, state0);
			controller.sendData(0, state1);
			controller.sendData(0, state2);
			controller.sendData(0, state3);
		} catch (UnsuccessfulRequestException e) {
			e.printStackTrace();
		}
	}

}
