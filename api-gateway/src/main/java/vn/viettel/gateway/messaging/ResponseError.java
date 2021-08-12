package vn.viettel.gateway.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

public class ResponseError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;

    public ResponseError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
