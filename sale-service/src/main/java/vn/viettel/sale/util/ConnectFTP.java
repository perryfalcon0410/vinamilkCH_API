package vn.viettel.sale.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import vn.viettel.core.exception.ApplicationException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

//    @Value( "${directory.tmp}" )
//    private String directoryTmp;

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

    public HashMap<String, InputStream> getFiles(String locationPath, String containsStr, String shopCode){
        HashMap<String,InputStream> mapinputStreams = new HashMap<>();
       try {
            if(StringUtils.stringIsNullOrEmpty(locationPath)){
                locationPath = "/" + "kch_pos" + "/" + "neworder";
            }
            if(containsStr == null) containsStr = "";

            if(ftpClient != null && ftpClient.isConnected() ){

                boolean status = ftpClient.changeWorkingDirectory(locationPath);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                if(status){
                    FTPFile[] ftpFiles = ftpClient.listFiles();
                    if (ftpFiles != null && ftpFiles.length > 0) {
                        for (FTPFile file : ftpFiles) {
                            if (file.isFile() && file.getName().endsWith(readFile) &&
                            ((shopCode==null || shopCode.isEmpty()) || (shopCode!=null && file.getName().toLowerCase().startsWith(shopCode.trim().toLowerCase())))
                                && file.getName().toLowerCase().contains(containsStr.toLowerCase())) {
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                ftpClient.retrieveFile(file.getName(), outputStream);
                                InputStream inputstream = new ByteArrayInputStream(outputStream.toByteArray());
                                mapinputStreams.put(file.getName(), inputstream);
                                outputStream.close();
                            }
                        }
                    }
                }
            }
        }catch (Exception ex) {
           ex.printStackTrace();
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
            ex.printStackTrace();
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

/*                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ftpClient.retrieveFile(fromFile, outputStream);
                    InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

                    // assuming backup directory is with in current working directory
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//binary files
                    ftpClient.changeWorkingDirectory(toPath);
                    //this overwrites the existing file
                    ftpClient.storeUniqueFile(destinationFile, is);
                    is.close();
                    outputStream.close();*/

                   ftpClient.deleteFile(fromFile );
                } else {
                    Path source = Paths.get(fromFile);
                    Path target = Paths.get(toFile);
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
                return true;
            }
        }catch (Exception ex) {
            ex.printStackTrace();
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
