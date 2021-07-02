package vn.viettel.sale.util;

import com.jcraft.jsch.*;
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
import java.util.Vector;

public class ConnectFTP {

    private ChannelSftp channelSftp;
    private Session jschSession = null;
    private String readFile = ".xml";

    public ConnectFTP(String server, String portStr, String userName, String password) {
        if(!StringUtils.stringIsNullOrEmpty(server) && !StringUtils.stringIsNullOrEmpty(userName)){
            try {
                if(channelSftp == null){
                    int port = 22;

                    if(!StringUtils.stringIsNullOrEmpty(server))
                        port = Integer.parseInt(portStr);

                    JSch jsch = new JSch();
//                    File privateKey = new File(SFTPPRIVATEKEY);
//                    if(privateKey.exists() && privateKey.isFile())
//                        jsch.addIdentity(SFTPPRIVATEKEY);
                    jschSession = jsch.getSession(userName, server, port);
                    if (!StringUtils.stringIsNullOrEmpty(password)) {
                        jschSession.setPassword(password);
                    }
                    jschSession.setConfig("StrictHostKeyChecking", "no");
                    jschSession.setTimeout(60000);
                    //	        if (aliveMax != null) session.setServerAliveCountMax(aliveMax);
                    jschSession.connect();
                    channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
                    channelSftp.connect();
                }else if(channelSftp != null && !channelSftp.isConnected()){
                    channelSftp.connect();
                }
            } catch (Exception ex) {
                throw new ApplicationException("Can not connect to server "+server+": " + ex.getMessage());
            }
        }else{
            channelSftp = null;
        }
    }

    public void disconnectServer(){
        if(channelSftp != null && channelSftp.isConnected()){
            channelSftp.exit();
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    public boolean uploadFile(InputStream inputStream, String fileName, String locationPath ){
        try {
            if(inputStream != null && !StringUtils.stringIsNullOrEmpty(fileName)) {
                if (StringUtils.stringIsNullOrEmpty(locationPath)) {
                    locationPath = File.separator + "kch_pos" + File.separator + "downorderpos";
                }
                if (channelSftp != null && channelSftp.isConnected()) {
                    String path = "";
//            locationPath = locationPath.replaceAll("\\\\", "/");
                    for (String dir : locationPath.split("/")) {
                        if (!StringUtils.stringIsNullOrEmpty(dir)) {
                            path = path + "/" + dir;
                            try {
                                channelSftp.mkdir(path);
                            } catch (Exception ee) {
                            }
                        }
                    }
                    String remotePath = locationPath + File.separator + fileName;
                    channelSftp.put(inputStream, remotePath);
                } else {
//            locationPath = "C:\\" + locationPath;
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

    public HashMap<String,InputStream> getFiles(String locationPath, String containsStr){
        HashMap<String,InputStream> mapinputStreams = new HashMap<>();
        try {
            if(StringUtils.stringIsNullOrEmpty(locationPath)){
                locationPath = File.separator + "kch_pos" + File.separator + "neworder";
            }
            if(containsStr == null) containsStr = "";
            if(channelSftp != null && channelSftp.isConnected()){
                channelSftp.cd(locationPath);
                Vector<ChannelSftp.LsEntry> listOfFiles = channelSftp.ls(locationPath);
                for (ChannelSftp.LsEntry entry : listOfFiles){
                    if (entry.getFilename().endsWith(readFile) && entry.getFilename().contains(containsStr)) {
                        mapinputStreams.put(entry.getFilename(), channelSftp.get(locationPath + File.separator + entry.getFilename()));
                    }
                }
            }else{
                File folder = new File(locationPath);
                File[] listOfFiles = folder.listFiles();

                for (File file : listOfFiles){
                    if (file.isFile() && file.getName().endsWith(readFile) && file.getName().contains(containsStr)) {
                        mapinputStreams.put(file.getName(), new FileInputStream(file));
                    }
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
                String fromFile = fromPath + File.separator + fileName;
                if(StringUtils.stringIsNullOrEmpty(toPath)) toPath = "/backup";
                String toFile = toPath + File.separator + destinationFile;

                if (channelSftp != null && channelSftp.isConnected()) {
                    channelSftp.put(fromFile, toFile);
                } else {
                    Path source = Paths.get(fromFile);
//                    if (Files.exists(source))
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