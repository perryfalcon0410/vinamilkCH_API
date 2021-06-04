package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class FTPController extends BaseController {

    private final String root = "/commons/ftp";

    @Autowired
    ApParamService apParamService;

    private SSHClient setupSshj() throws IOException {
        ApParamDTO serverAP = apParamService.getByCode("SERVER_FTP");
        ApParamDTO user = apParamService.getByCode("USER_FTP");
        ApParamDTO pass = apParamService.getByCode("PASS_FTP");

        String strServer = serverAP.getValue();
        String strUser = user.getValue();
        String strPass = pass.getValue();
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(strServer);
        client.authPassword(strUser, strPass);
        return client;
    }

    @ApiOperation(value = "Test function FTP download file")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/test"})
    public Response<String> testFtp() throws IOException {
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();

        sftpClient.get("/home/kch/demo.txt", "/tmp/demo.txt");

        sftpClient.close();
        sshClient.disconnect();

        return new Response<String>().withData("DONE");
    }

}
