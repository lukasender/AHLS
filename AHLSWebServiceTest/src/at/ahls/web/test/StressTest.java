package at.ahls.web.test;

import java.io.IOException;

import at.ahls.web.test.util.HTTPMethod;
import at.ahls.web.test.util.HTTPRequest;

public class StressTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String url = "http://localhost:8080/AHLSWebService/ahls/log";
		
		long time = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			String message = "{\"@data\": \"" + i + "\"}";
			HTTPRequest request = new HTTPRequest(url, message, HTTPMethod.POST);
			try {
				request.connect();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long finished = System.currentTimeMillis() - time;
		
		System.out.println("StressTest performed in " + finished + "ms");

	}

}
