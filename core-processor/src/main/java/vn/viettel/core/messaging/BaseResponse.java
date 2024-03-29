package vn.viettel.core.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;

import java.time.LocalDateTime;

public abstract class BaseResponse<T> {

	/**
	 * Success parameter which return to client
	 */
	private Boolean success = true;

	/**
	 * Status code parameter which return to client
	 */
	private Integer statusCode = ResponseMessage.SUCCESSFUL.statusCode();

	/**
	 * Status value parameter which return to client
	 */
	private String statusValue = ResponseMessage.SUCCESSFUL.statusCodeValue();

	/**
	 * ExcuteDate parameter which return to client. Default is current time.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
	private LocalDateTime executeDate = LocalDateTime.now();

	/**
	 * Data type T: parameter which return to client. Default is null.
	 */
	protected T data = null;

	/**
	 * Data type new Token : the new generated token after a successfully request
	 */
	private String token = null;

	/**
	 * Validate data passing
	 * 
	 * @param data       T
	 * @param statusCode : status code if error
	 * @param            statusValue: status value if error
	 */
	public void validated(T data, Integer statusCode, String statusValue) {
		if (data == null) {
			setFailure(statusCode, statusValue);
		} else {
			setData(data);
		}
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
	}

	public LocalDateTime getExecuteDate() {
		return executeDate;
	}

	public void setExecuteDate(LocalDateTime executeDate) {
		this.executeDate = executeDate;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public void setFailure(int statusCode, String statusValue) {
		this.setSuccess(false);
		this.setStatusCode(statusCode);
		this.setStatusValue(statusValue);
	}

	public void setFailure(ResponseMessage responseMessage) {
		setFailure(responseMessage.statusCode(), responseMessage.statusCodeValue());
	}

	public void setFailure(String smg) {
		setFailure(500, smg);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
