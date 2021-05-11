package vn.viettel.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	
	private HttpStatus status = HttpStatus.NOT_FOUND;
	private String title = " not found";

    public ResourceNotFoundException( Class resourceClazz, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceClazz == null ? "" : resourceClazz.getSimpleName(), fieldName, fieldValue));
        title = resourceClazz == null ? "" : resourceClazz.getSimpleName() + title;
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
        title = "Resources" + title;
    }

	public HttpStatus getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}
}