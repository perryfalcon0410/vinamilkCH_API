package vn.viettel.core.controller;

import org.apache.http.HttpRequest;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;

@RequestMapping("/api")
public abstract class BaseController extends HandlerException {

    public final String V1 = "v1";
    public final String V2 = "v2";

    @Value("${spring.application.name}")
    public String appName;

    @Autowired
    private SecurityContexHolder securityContexHolder;
/*
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

    public String getUserName() {
        return securityContexHolder.getContext().getUserName();
    }*/

    public Long getShopId(HttpServletRequest httpRequest) {
        Long shopId = (Long) httpRequest.getAttribute(Constants.CURRENT_SHOP_ID);
        if(shopId !=null) return shopId;
        return securityContexHolder.getContext().getShopId();
    }

    public Long getUserId(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(Constants.CURRENT_USER_ID);
        if(userId !=null) return userId;
        return securityContexHolder.getContext().getUserId();
    }

    public String getUsername(HttpServletRequest httpRequest) {
        String username = (String)  httpRequest.getAttribute(Constants.CURRENT_USERNAME);
        if(username !=null && !username.isEmpty()) return username;
        return securityContexHolder.getContext().getUserName();
    }

    public HttpServletResponse closeStreamExcel(HttpServletResponse response, ByteArrayInputStream in, String fileName) {
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName + StringUtils.createExcelFileName());
        try {
            FileCopyUtils.copy(in, response.getOutputStream());
        }catch (Exception e) {
            throw new ValidateException(ResponseMessage.EXPORT_EXCEL_FAILED);
        }finally {
            IOUtils.closeQuietly(in);
        }
        return response;
    }

}
