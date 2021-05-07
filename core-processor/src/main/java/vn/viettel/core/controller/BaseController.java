package vn.viettel.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.security.context.SecurityContexHolder;

@RequestMapping("/api")
public abstract class BaseController extends HandlerException {

    public final String V1 = "v1";
    public final String V2 = "v2";

    @Autowired
    private SecurityContexHolder securityContexHolder;

    public String getRole() {
        return securityContexHolder.getContext().getRole();
    }

    public Long getRoleId() {
        return securityContexHolder.getContext().getRoleId();
    }

    public Long getShopId() {
        return securityContexHolder.getContext().getShopId();
    }

    public Long getUserId() {
        return securityContexHolder.getContext().getUserId();
    }
}
