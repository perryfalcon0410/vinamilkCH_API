package vn.viettel.common.service.impl;

import org.apache.commons.net.ftp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.common.service.ApParamService;
import vn.viettel.common.service.FTPService;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FTPServiceImpl implements FTPService {

    @Autowired
    ApParamService apParamService;

    AtomicReference<String> strFolderRemote = new AtomicReference<>("");
    AtomicReference<String> strFolderLocal = new AtomicReference<>("");

    private FTPClient setupSTP() throws IOException {
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
                case "FTP_PORT":
                    strPost.set(apParamDTO.getValue());
                    break;
                default:
                    break;
            }
        });

        FTPClient ftpClient = new FTPClient();
        int FTP_TIMEOUT = 60000;

        try {
            System.out.println("connecting ftp server...");
            // connect to ftp server
            ftpClient.setDefaultTimeout(FTP_TIMEOUT);
            ftpClient.connect(strServer.get(), Integer.parseInt(strPost.get()));
            // run the passive mode command
            ftpClient.enterLocalPassiveMode();
            // check reply code
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                disconnectFTPServer(ftpClient);
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
        return ftpClient;
    }


    /**
     * disconnect ftp server
     */
    private void disconnectFTPServer(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public Response<String> downloadFtp() throws IOException {
        FTPClient ftpClient = setupSTP();

        // get list file ends with (.txt) from ftp server
        List<FTPFile> listFiles =
                getListFileFromFTPServer(ftpClient, strFolderRemote.get(), "txt");
        // download list file from ftp server and save to "D:"
        for (FTPFile ftpFile : listFiles) {
            downloadFTPFile(ftpClient, strFolderRemote.get() + "/" + ftpFile.getName(), strFolderLocal.get() + "/" + ftpFile.getName());
        }

        disconnectFTPServer(ftpClient);
        return new Response<String>().withData("DONE");
    }

    /**
     * using method retrieveFile(String, OutputStream)
     * to download file from FTP Server
     *
     * @param ftpFilePath
     * @param downloadFilePath
     */
    private void downloadFTPFile(FTPClient ftpClient, String ftpFilePath, String downloadFilePath) {
        System.out.println("File " + ftpFilePath + " is downloading...");
        OutputStream outputStream = null;
        boolean success = false;
        try {
            File downloadFile = new File(downloadFilePath);
            outputStream = new BufferedOutputStream(
                    new FileOutputStream(downloadFile));
            // download file from FTP Server
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024 * 1024 * 1);
            success = ftpClient.retrieveFile(ftpFilePath, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (success) {
            System.out.println("File " + ftpFilePath
                    + " has been downloaded successfully.");
        }
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


    /**
     * using method retrieveFile(String, OutputStream)
     * to download file from FTP Server
     *
     * @param ftpFilePath
     * @param uploadFilePath
     */
    private void uploadFTPFile(FTPClient ftpClient, String ftpFilePath, String uploadFilePath) {
        System.out.println("File " + ftpFilePath + " is uploading...");
        InputStream inputStream = null;
        boolean success = false;
        try {
            File uploadFile = new File(uploadFilePath);
            inputStream = new FileInputStream(uploadFile);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            success = ftpClient.storeFile(ftpFilePath, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (success) {
            System.out.println("File " + ftpFilePath
                    + " has been uploaded successfully.");
        }
    }


    @Override
    public Response<String> uploadFtp() throws IOException {
        FTPClient ftpClient = setupSTP();

        final File folder = new File(strFolderLocal.get() + "/test");

        List<String> result = new ArrayList<>();

        search(".*\\.txt", folder, result);

        ftpClient.makeDirectory(strFolderRemote.get() + "/upload/");

        for (String fileName : result) {
            System.out.println(fileName);
            uploadFTPFile(ftpClient, strFolderRemote.get() + "/upload/" + fileName, strFolderLocal.get() + "/test/" + fileName);
        }

        disconnectFTPServer(ftpClient);

        return new Response<String>().withData("DONE");
    }

    private void search(final String pattern, final File folder, List<String> result) {
        for (final File f : Objects.requireNonNull(folder.listFiles())) {

            if (f.isDirectory()) {
                search(pattern, f, result);
            }

            if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    result.add(f.getName());
                }
            }

        }
    }

}
