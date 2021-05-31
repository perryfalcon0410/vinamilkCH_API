package vn.viettel.common.controller;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
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

    private SSHClient setupSshj() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect("192.168.100.112");
        client.authPassword("kch", "Viett3l$Pr0ject");
        return client;
    }

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
