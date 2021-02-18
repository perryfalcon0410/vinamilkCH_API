package vn.viettel.core.exception;

import vn.viettel.core.ResponseMessage;

import java.util.List;

public class FeatureNotAvailableException extends Exception {
    private List<String> itemDisabled;

    private ResponseMessage responseMessage;

    public FeatureNotAvailableException(ResponseMessage responseMessage) {
        super(responseMessage.statusCodeValue());
    }

    public FeatureNotAvailableException(ResponseMessage responseMessage, List<String> itemDisabled) {
        super(responseMessage.statusCodeValue());
        this.itemDisabled = itemDisabled;
    }

    public List<String> getItemDisabled() {
        return itemDisabled;
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
