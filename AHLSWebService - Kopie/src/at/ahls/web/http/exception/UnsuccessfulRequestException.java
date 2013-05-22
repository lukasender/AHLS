package at.ahls.web.http.exception;

public class UnsuccessfulRequestException extends Exception {
	
	private static final long serialVersionUID = 5513597727206567007L;

	public UnsuccessfulRequestException() {
		super();
	}
	
	public UnsuccessfulRequestException(String message) {
		super(message);
	}
}
