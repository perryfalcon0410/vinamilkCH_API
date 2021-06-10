package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class FTPController extends BaseController {

    private final String root = "/commons/ftp";

    @Autowired
    ApParamService apParamService;

    AtomicReference<String> strFolderRemote = new AtomicReference<>("");
    AtomicReference<String> strFolderLocal = new AtomicReference<>("");

    private SSHClient setupSshj() throws IOException {
        List<ApParamDTO> apParamDTOList = apParamService.getByType("FTP");

        AtomicReference<String> strServer = new AtomicReference<>("");
        AtomicReference<String> strUser = new AtomicReference<>("");
        AtomicReference<String> strPass = new AtomicReference<>("");

        apParamDTOList.forEach(apParamDTO -> {
            switch (apParamDTO.getApParamCode()) {
                case "SERVER_FTP":
                    strServer.set(apParamDTO.getValue());
                    break;
                case "USER_FTP":
                    strUser.set(apParamDTO.getValue());
                    break;
                case "PASS_FTP":
                    strPass.set(apParamDTO.getValue());
                    break;
                case "FOLDER_REMOTE_FTP":
                    strFolderRemote.set(apParamDTO.getValue());
                    break;
                case "FOLDER_LOCAL_FTP":
                    strFolderLocal.set(apParamDTO.getValue());
                    break;
                default:
                    break;
            }
        });

        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(strServer.get());
        client.authPassword(strUser.get(), strPass.get());
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

        sftpClient.get(strFolderRemote.get(), strFolderLocal.get());

        sftpClient.close();
        sshClient.disconnect();

        return new Response<String>().withData("DONE");
    }

}
