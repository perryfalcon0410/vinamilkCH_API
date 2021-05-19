package vn.viettel.core.messaging;

import vn.viettel.core.util.ResponseMessage;

public class Response<D> extends BaseResponse<D> {

    public Response<D> withData(D data) {
        this.setData(data);
        return this;
    }

    public Response<D> withError(ResponseMessage msg) {
        this.setFailure(msg);
        return this;
    }

    public Response<D> withError(String msg) {
        this.setFailure(msg);
        return this;
    }
}
