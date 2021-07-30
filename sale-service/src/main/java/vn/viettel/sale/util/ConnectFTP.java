package vn.viettel.sale.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.exception.ApplicationException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ConnectFTP {

    private FTPClient ftpClient;
    private String readFile = ".xml";

    public ConnectFTP(String server, String portStr, String userName, String password) {
        AtomicReference<String> strServer = new AtomicReference<>("");
        AtomicReference<String> strUser = new AtomicReference<>("");
        AtomicReference<String> strPass = new AtomicReference<>("");
        AtomicReference<String> strPort = new AtomicReference<>("");
        strServer.set(server);
        strUser.set(userName);
        strPass.set(password);
        strPort.set(portStr);

        ftpClient = new FTPClient();
        int FTP_TIMEOUT = 60000;

        try {
            System.out.println("connecting ftp server...");
            // connect to ftp server
            ftpClient.setDefaultTimeout(FTP_TIMEOUT);
            ftpClient.connect(strServer.get(), Integer.parseInt(strPort.get()));
            // run the passive mode command
            ftpClient.enterLocalPassiveMode();
            // check reply code
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                disconnectFTPServer();
                throw new IOException("FTP server not respond!");
            } else {
                ftpClient.setSoTimeout(FTP_TIMEOUT);
                // login ftp server
                if (!ftpClient.login(strUser.get(), strPass.get())) {
                    throw new IOException("Username or password is incorrect!");
                }
                ftpClient.setDataTimeout(FTP_TIMEOUT);
                System.out.println("connected");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public HashMap<String, InputStream> getFiles(String locationPath, String containsStr){
        HashMap<String,InputStream> mapinputStreams = new HashMap<>();
        try {
            if(StringUtils.stringIsNullOrEmpty(locationPath)){
                locationPath = "/" + "kch_pos" + "/" + "neworder";
            }
            if(containsStr == null) containsStr = "";

            File directory = new File(locationPath);
            if (! directory.exists()){
                directory.mkdir();
            }

            if(ftpClient != null && ftpClient.isConnected()){
                File[] listOfFiles = directory.listFiles();
                for (File file : listOfFiles){
                    file.delete();
                }



                // FileUtils.cleanDirectory(directory);
//                SFTPClient sftpClient = ftpClient.newSFTPClient();
//                sftpClient.get(locationPath, locationPath);
//                sftpClient.close();
            }

            File[] listOfFiles = directory.listFiles();
            for (File file : listOfFiles){
                if (file.isFile() && file.getName().endsWith(readFile) && file.getName().contains(containsStr)) {
                    mapinputStreams.put(file.getName(), new FileInputStream(file));
                }
            }
        }catch (Exception ex) {
            LogFile.logToFile("", "", LogLevel.ERROR, null, "FTP read files error: " + ex.getMessage());
        }

        return mapinputStreams;
    }



    /**
     * get list from ftp server
     *
     * @param path
     * @return List<FTPFile>
     */
    private List<FTPFile> getListFileFromFTPServer(FTPClient ftpClient, String path, final String ext) {
        List<FTPFile> listFiles = new ArrayList<FTPFile>();
        try {
            // list file ends with "jar"
            FTPFile[] ftpFiles = ftpClient.listFiles(path, new FTPFileFilter() {
                public boolean accept(FTPFile file) {
                    return file.getName().endsWith(ext);
                }
            });
            if (ftpFiles.length > 0) {
                for (FTPFile ftpFile : ftpFiles) {
                    // add file to listFiles
                    if (ftpFile.isFile()) {
                        listFiles.add(ftpFile);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listFiles;
    }

    public void disconnectFTPServer() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
