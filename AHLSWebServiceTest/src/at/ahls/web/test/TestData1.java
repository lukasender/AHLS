package at.ahls.web.test;

import java.io.IOException;

import at.ahls.web.test.util.HTTPMethod;
import at.ahls.web.test.util.HTTPRequest;

public class TestData1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String url = "http://localhost:8080/AHLSWebService/ahls/log";
		
		long time = System.currentTimeMillis();
		for (double i = -10; i < 10; i+=0.1) {
			
			// Since we won't have negative activity data, I use Math.abs().
			double y = Math.round(Math.abs(polynomial(i)) + 0.05d);
			if (y > 1023) {
				y = 1023;
			}
			
			String message = "{\"@data\": \"" + y + "\"}";
			System.out.println(message);
			
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
	
	public static double polynomial(double x) {
		/*
		 * http://commons.wikimedia.org/wiki/File:Polynomialdeg5.svg
		 * f(x) = (x+4) (x+2) (x+1) (x-1) (x-3) * 1/20 + 2
		 */
		double y = (x+3) * (x+2) * (x+1) + (x-1) * (x-3) * 1/20.0 + 2;
		
		return y;
	}

}
