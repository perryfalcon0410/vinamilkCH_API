package vn.viettel.sale.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import vn.viettel.core.exception.ApplicationException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                if(status){
                    FTPFile[] lstFiles = ftpClient.listFiles();
                    InputStream inputStream = null;

                    for (int i = 0; i < lstFiles.length; i++) {
                        if (lstFiles[i].isFile() && lstFiles[i].getName().endsWith(readFile) && lstFiles[i].getName().contains(containsStr)) {
                            inputStream = ftpClient.retrieveFileStream(lstFiles[i].getName());
                            if (inputStream != null) {
                                inputStream.mark(0);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = inputStream.read(buffer)) > -1 ) {
                                    baos.write(buffer, 0, len);
                                }
                                baos.flush();
                                mapinputStreams.put(lstFiles[i].getName(), new ByteArrayInputStream(baos.toByteArray()));
                                inputStream.reset();
                            }
                        }
                    }
                }
            }
        }catch (Exception ex) {
            LogFile.logToFile("", "", LogLevel.ERROR, null, "FTP read files error: " + ex.getMessage());
        }

        return mapinputStreams;
    }

    public boolean uploadFile(InputStream inputStream, String fileName, String locationPath ){
        try {
            if(inputStream != null && !StringUtils.stringIsNullOrEmpty(fileName)) {
                if (StringUtils.stringIsNullOrEmpty(locationPath)) {
                    locationPath = "/" + "kch_pos" + "/" + "downorderpos";
                }
                if(ftpClient != null && ftpClient.isConnected()){
                    ftpClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    ftpClient.storeFile(locationPath + "/" + fileName, inputStream);
                    ftpClient.sendNoOp();
                } else {
                    Path pathLocation = Paths.get(locationPath).toAbsolutePath().normalize();
                    Files.createDirectories(pathLocation);
                    Path targetLocation = pathLocation.resolve(fileName);
                    Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                }
                return true;
            }
        }catch (Exception ex) {
            LogFile.logToFile("", "", LogLevel.ERROR, null, "FTP upload error: " + ex.getMessage());
        }

        return false;
    }

    public boolean moveFile(String fromPath, String toPath, String fileName){
        try {
            if(!StringUtils.stringIsNullOrEmpty(fileName)) {
                String destinationFile = "ReadAt" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + fileName;
                String fromFile = fromPath + "/" + fileName;
                if(StringUtils.stringIsNullOrEmpty(toPath)) toPath = "/backup";
                String toFile = toPath + "/" + destinationFile;
                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.rename(fromFile ,toFile);
                    ftpClient.deleteFile(fromFile );
                } else {
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
