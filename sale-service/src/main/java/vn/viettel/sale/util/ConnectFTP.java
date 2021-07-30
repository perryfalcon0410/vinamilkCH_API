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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            throw new ApplicationException("Can not connect to server "+server+": " + ex.getMessage());
        }
    }

    public HashMap<String, InputStream> getFiles(String locationPath, String containsStr){
        HashMap<String,InputStream> mapinputStreams = new HashMap<>();
        try {
            if(StringUtils.stringIsNullOrEmpty(locationPath)){
                locationPath = "/" + "kch_pos" + "/" + "neworder";
            }
            if(containsStr == null) containsStr = "";

            if(ftpClient != null && ftpClient.isConnected()){
                boolean status = ftpClient.changeWorkingDirectory(locationPath);
                if(status){
                    FTPFile[] lstFiles = ftpClient.listFiles();
                    for (int i = 0; i < lstFiles.length; i++) {
                        InputStream inputStream = ftpClient.retrieveFileStream(lstFiles[i].getName());
                        if (inputStream != null) {
                            mapinputStreams.put(lstFiles[i].getName(), inputStream);
                        }
                    }
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


    public boolean moveFile(String fromPath, String toPath, String fileName){
        try {
            if(!StringUtils.stringIsNullOrEmpty(fileName)) {
                String destinationFile = "ReadAt" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + fileName;
                String fromFile = fromPath + "/" + fileName;
                if(StringUtils.stringIsNullOrEmpty(toPath)) toPath = "/backup";
                String toFile = toPath + "/" + destinationFile;

                if(ftpClient != null && ftpClient.isConnected()){
                    ftpClient.rename(fromFile, toFile);
              //      ftpClient.removeDirectory(fromFile);
//                    SFTPClient sftpClient = client.newSFTPClient();
//                    sftpClient.rename(fromFile ,toFile);
//                    sftpClient.rm(fromFile );
//                    sftpClient.close();
                } else {
                    File directory = new File(toPath);
                    if (! directory.exists()){
                        directory.mkdir();
                    }
                    Path source = Paths.get(fromFile);
                    Path target = Paths.get(toFile);
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
                return true;
            }
        }catch (Exception ex) {
            LogFile.logToFile("", "", LogLevel.ERROR, null, "FTP move file error: " + ex.getMessage());
        }
        return false;
    }



}
