package vn.viettel.authorization.controller;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.user.UploadImageRequest;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleDistributor;
import vn.viettel.core.security.anotation.RoleShopOwner;
import vn.viettel.core.service.UploadImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UploadImageController extends HandlerException {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UploadImageService service;

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @PostMapping("/api/user/upload")
    public Response<String> upload(@Valid @RequestBody UploadImageRequest request) {
        logger.info("[upload()] - user upload image");
        Response<String> response = new Response<>();
        String url = service.uploadToTempFolderAndGetUrl(request.getImage());
        response.setData(url);
        return response;
    }
}
