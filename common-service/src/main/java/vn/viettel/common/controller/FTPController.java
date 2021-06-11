package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.FTPService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;

import java.io.IOException;


@RestController
public class FTPController extends BaseController {

    private final String root = "/commons/ftp";


    @Autowired
    private FTPService ftpService;

    @ApiOperation(value = "Test function FTP Uploading  file")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/upload"})
    public Response<String> uploadFtp() throws IOException {
        return ftpService.uploadFtp();
    }

    @ApiOperation(value = "Test function Downloading do file")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/download"})
    public Response<String> downloadFtp() throws IOException {
        return ftpService.downloadFtp();
    }

}
