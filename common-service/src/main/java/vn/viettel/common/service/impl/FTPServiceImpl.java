package vn.viettel.common.service.impl;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.common.service.ApParamService;
import vn.viettel.common.service.FTPService;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FTPServiceImpl implements FTPService {

    @Autowired
    ApParamService apParamService;

    AtomicReference<String> strFolderRemote = new AtomicReference<>("");
    AtomicReference<String> strFolderLocal = new AtomicReference<>("");

    private SSHClient setupSshj() throws IOException {
        List<ApParamDTO> apParamDTOList = apParamService.getByType("FTP");

        AtomicReference<String> strServer = new AtomicReference<>("");
        AtomicReference<String> strUser = new AtomicReference<>("");
        AtomicReference<String> strPass = new AtomicReference<>("");
        AtomicReference<String> strPost = new AtomicReference<>("");

        apParamDTOList.forEach(apParamDTO -> {
            switch (apParamDTO.getApParamCode()) {
                case "FTP_SERVER":
                    strServer.set(apParamDTO.getValue());
                    break;
                case "FTP_USER":
                    strUser.set(apParamDTO.getValue());
                    break;
                case "FTP_PASS":
                    strPass.set(apParamDTO.getValue());
                    break;
                case "FOLDER_REMOTE_FTP":
                    strFolderRemote.set(apParamDTO.getValue());
                    break;
                case "FOLDER_LOCAL_FTP":
                    strFolderLocal.set(apParamDTO.getValue());
                    break;
                case "PORT_FTP":
                    strPost.set(apParamDTO.getValue());
                    break;
                default:
                    break;
            }
        });

        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(strServer.get(), Integer.valueOf(strPost.get()));
        client.authPassword(strUser.get(), strPass.get());
        return client;
    }


    @Override
    public Response<String> downloadFtp() throws IOException {
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();

        sftpClient.get(strFolderRemote.get(), strFolderLocal.get());

        sftpClient.close();
        sshClient.disconnect();

        return new Response<String>().withData("DONE");
    }

    @Override
    public Response<String> uploadFtp() throws IOException {
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();

        sftpClient.put(strFolderLocal.get(), strFolderRemote.get());

        sftpClient.close();
        sshClient.disconnect();

        return new Response<String>().withData("DONE");
    }
}
