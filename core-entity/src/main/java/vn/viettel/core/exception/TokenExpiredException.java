package vn.viettel.core.exception;

import vn.viettel.core.ResponseMessage;

public class TokenExpiredException extends Exception {
    private ResponseMessage responseMessage;

    public TokenExpiredException(ResponseMessage responseMessage) {
        super(responseMessage.statusCodeValue());
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
