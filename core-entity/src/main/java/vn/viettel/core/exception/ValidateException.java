package vn.viettel.core.exception;

import vn.viettel.core.util.ResponseMessage;

public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private ResponseMessage msg;

    public ValidateException(ResponseMessage msg) {
        this.msg = msg;
    }

    public ResponseMessage getMsg() {
        return msg;
    }

    public void setMsg(ResponseMessage msg) {
        this.msg = msg;
    }
}
