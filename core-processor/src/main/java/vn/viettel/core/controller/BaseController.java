package vn.viettel.core.controller;

import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.security.context.SecurityContexHolder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController extends HandlerException {

    @Autowired
    private SecurityContexHolder securityContexHolder;

    public String getRole() {
        return securityContexHolder.getContext().getRole();
    }

    public Long getRoleId() {
        return securityContexHolder.getContext().getRoleId();
    }

    public Long getUserId() {
        return securityContexHolder.getContext().getUserId();
    }
}
