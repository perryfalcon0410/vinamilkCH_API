package vn.viettel.core.exception;

import vn.viettel.core.util.ResponseMessage;

public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private ResponseMessage msg;
    private String customMsg;

    public ValidateException(ResponseMessage msg) {
        this.msg = msg;
    }

    public ValidateException(ResponseMessage msg, String... args) {
        this.msg = msg;
        this.customMsg = String.format( msg.statusCodeValue(), args);
    }

    public String getMessage(){
        if (customMsg != null && !customMsg.trim().equals("")){
            return customMsg;
        }
        return msg.statusCodeValue();
    }

    public int getStaus(){
        return msg.statusCode();
    }

    public ResponseMessage getMsg() {
        return msg;
    }

    public void setMsg(ResponseMessage msg) {
        this.msg = msg;
    }
}
