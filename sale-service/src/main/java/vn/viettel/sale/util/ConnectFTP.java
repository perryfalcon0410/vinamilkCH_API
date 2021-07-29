package vn.viettel.sale.util;

import vn.viettel.core.exception.ApplicationException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public class ConnectFTP {

    private SSHClient client;
    private String readFile = ".xml";

    public ConnectFTP(String server, String portStr, String userName, String password) {
        if(!StringUtils.stringIsNullOrEmpty(server) && !StringUtils.stringIsNullOrEmpty(userName)){
            AtomicReference<String> strServer = new AtomicReference<>("");
            AtomicReference<String> strUser = new AtomicReference<>("");
            AtomicReference<String> strPass = new AtomicReference<>("");
            AtomicReference<String> strPort= new AtomicReference<>("");
            strServer.set(server);
            strUser.set(userName);
            strPass.set(password);
            strPort.set(portStr);

            try {
                client = new SSHClient();
                client.addHostKeyVerifier(new PromiscuousVerifier());
                client.connect(strServer.get(), Integer.valueOf(strPort.get()));
                client.authPassword(strUser.get(), strPass.get());
            } catch (Exception ex) {
                throw new ApplicationException("Can not connect to server "+server+": " + ex.getMessage());
            }
        }else{
            client = null;
        }
    }

    public void disconnectServer(){
        if(client != null && client.isConnected()){
            try {
                client.close();
                client.disconnect();
            } catch (Exception ex) {
                throw new ApplicationException("Can not disconnect to server: " + ex.getMessage());
            }
        }
    }

    public boolean uploadFile(InputStream inputStream, String fileName, String locationPath ){
        try {
            if(inputStream != null && !StringUtils.stringIsNullOrEmpty(fileName)) {
                if (StringUtils.stringIsNullOrEmpty(locationPath)) {
                    locationPath = "/" + "kch_pos" + "/" + "downorderpos";
                }
                Path pathLocation = Paths.get(locationPath).toAbsolutePath().normalize();
                Files.createDirectories(pathLocation);
                Path targetLocation = pathLocation.resolve(fileName);
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

                if(client != null && client.isConnected()){
                    SFTPClient sftpClient = client.newSFTPClient();
                    String remotePath = locationPath + "/" + fileName;
                    sftpClient.put(remotePath, remotePath);
                    sftpClient.close();
                }
                return true;
            }
        }catch (Exception ex) {
            LogFile.logToFile("", "", LogLevel.ERROR, null, "FTP upload error: " + ex.getMessage());
        }

        return false;
    }

    public HashMap<String,InputStream> getFiles(String locationPath, String containsStr){
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


            if(client != null && client.isConnected()){
                File[] listOfFiles = directory.listFiles();
                for (File file : listOfFiles){
                    file.delete();
                }
               // FileUtils.cleanDirectory(directory);

                SFTPClient sftpClient = client.newSFTPClient();
                sftpClient.get(locationPath, locationPath);
                sftpClient.close();
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

    public boolean moveFile(String fromPath, String toPath, String fileName){
        try {
            if(!StringUtils.stringIsNullOrEmpty(fileName)) {
                String destinationFile = "ReadAt" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + fileName;
                String fromFile = fromPath + "/" + fileName;
                if(StringUtils.stringIsNullOrEmpty(toPath)) toPath = "/backup";
                String toFile = toPath + "/" + destinationFile;

                if(client != null && client.isConnected()){
                    SFTPClient sftpClient = client.newSFTPClient();
                    sftpClient.rename(fromFile ,toFile);
                    sftpClient.rm(fromFile );
                    sftpClient.close();
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

    public void setReadFile(String readFile){
        if(!StringUtils.stringIsNullOrEmpty(readFile)) this.readFile = readFile;
    }
}
