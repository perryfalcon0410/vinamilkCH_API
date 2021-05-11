package vn.viettel.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
	
	private HttpStatus status = HttpStatus.BAD_REQUEST;
	private String title = "Bad request";

	public BadRequestException(String message) {
        super(message);
    }

	public HttpStatus getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}
}
