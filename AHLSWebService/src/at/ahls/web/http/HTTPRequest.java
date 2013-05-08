package at.ahls.web.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import at.ahls.web.http.exception.UnsuccessfulRequestException;

public class HTTPRequest {

	private String _url;
	private String _message;
	private HTTPMethod _method;
	

	public HTTPRequest(String url, String message, HTTPMethod method) {
		_url = url;
		_message = message;
		_method = method;
	}
	
	public void connect() throws UnsuccessfulRequestException {
		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		
		try {
			connection = (HttpURLConnection) new URL(_url).openConnection();
			
			System.out.println(_method.name());
			connection.setRequestMethod(_method.name());
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			
			// get the output stream writer and write the output to the server
			// not needed in this example
			wr = new OutputStreamWriter(connection.getOutputStream());
			System.out.println(_message);
			wr.write(_message);
			wr.flush();
			
			System.out.println(connection.getResponseCode());
			if (connection.getResponseCode() != 200) {
				throw new UnsuccessfulRequestException("Response message: " + connection.getResponseCode() + ";\n" + connection.getResponseMessage());
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					// ignore
				}
				wr = null;
			}
		}
	}

}
