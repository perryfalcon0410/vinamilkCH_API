package vn.viettel.core.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.Base64;
import vn.viettel.core.util.ImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class UploadImageProcessor {

    private static final String TEMP_IMAGE_FOLDER = "TEMP";
    private AmazonS3 s3;

    @Value("${amazon.s3.access-key}")
    private String accessKey;

    @Value("${amazon.s3.secret-key}")
    private String secretKey;

    @Value("${amazon.s3.regions}")
    private String regions;

    @Value("${amazon.s3.bucket}")
    private String bucket;

    @Value("${amazon.s3.base-folder}")
    private String baseFolder;

    @PostConstruct
    private void initializeAmazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .withRegion(regions)
            .build();
    }

    public String uploadImageToTempFolderAndGetUrl(String imageData) {
        String base64 = ImageUtils.getBase64(imageData);
        byte[] bImage = Base64.decode(base64);
        InputStream inputStream = new ByteArrayInputStream(bImage);
        String ext = ImageUtils.getFileExtension(imageData);
        String path = getTempUploadPath();
        String fileName = generateImageFileName(ext);

        String contentType = ImageUtils.getContentType(imageData);
        int contentLength = bImage.length;
        ObjectMetadata metadata = createMetadata(contentType, contentLength);

        PutObjectRequest request = createPutObjectRequest(path, fileName, inputStream, metadata);
        s3.putObject(request);
        return s3.getUrl(path, fileName).toString();
    }

    public String asyncChangeTempImageIntoOfficialImageAndGetUrl(String officialFolder, String fileName) {
        ThreadChangeTempImageToOfficialImage runnable = new ThreadChangeTempImageToOfficialImage(officialFolder, fileName);
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        return getAsyncImageShortPath(officialFolder, fileName);
    }

    public void asyncDeleteImageInOfficialFolder(String officialFolder, String fileName) {
        ThreadDeleteImageInOfficialFolder runnable = new ThreadDeleteImageInOfficialFolder(officialFolder, fileName);
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private ObjectMetadata createMetadata(String contentType, int contentLength) {
        String cacheControl = "public, max-age=31536000";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(contentLength);
        metadata.setCacheControl(cacheControl);
        return metadata;
    }

    private PutObjectRequest createPutObjectRequest(String path, String fileName, InputStream inputStream, ObjectMetadata metadata) {
        CannedAccessControlList readOnly = CannedAccessControlList.PublicRead;
        PutObjectRequest request = new PutObjectRequest(path, fileName, inputStream, metadata)
            .withCannedAcl(readOnly);
        return request;
    }

    private CopyObjectRequest createCopyObjectRequest(String tempPath, String offcialPath, String fileName) {
        CannedAccessControlList readOnly = CannedAccessControlList.PublicRead;
        CopyObjectRequest request = new CopyObjectRequest(tempPath, fileName, offcialPath, fileName)
            .withCannedAccessControlList(readOnly);
        return request;
    }

    private String getAsyncImageShortPath(String officialFolder, String fileName) {
        String officialUploadShortPath = getOfficialUploadShortPath(officialFolder);

        StringBuilder sb = new StringBuilder();
        return sb.append(officialUploadShortPath)
            .append("/")
            .append(fileName)
            .toString();
    }

    private String getTempUploadPath() {
        StringBuilder sb = new StringBuilder();
        return sb.append(bucket)
            .append("/")
            .append(TEMP_IMAGE_FOLDER)
            .toString();
    }

    private String getOfficialUploadPath(String folder) {
        StringBuilder sb = new StringBuilder();
        return sb.append(bucket)
            .append("/")
            .append(baseFolder)
            .append("/")
            .append(folder)
            .toString();
    }

    private String getOfficialUploadShortPath(String folder) {
        String pathRemoveEnv = StringUtils.substringAfterLast(baseFolder, "/"); // "dev/user" => "user"

        StringBuilder sb = new StringBuilder();
        return sb.append(pathRemoveEnv)
            .append("/")
            .append(folder)
            .toString();
    }

    private String generateImageFileName(String ext) {
        StringBuilder sb = new StringBuilder();
        return sb.append(System.currentTimeMillis())
            .append(".")
            .append(ext)
            .toString();
    }

    class ThreadChangeTempImageToOfficialImage implements Runnable {
        private String officialFolder;
        private String fileName;

        public ThreadChangeTempImageToOfficialImage(String officialFolder, String fileName) {
            this.officialFolder = officialFolder;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            String tempPath = getTempUploadPath();
            String officialPath = getOfficialUploadPath(officialFolder);
            CopyObjectRequest request = createCopyObjectRequest(tempPath, officialPath, fileName);
            s3.copyObject(request);
        }
    }

    class ThreadDeleteImageInOfficialFolder implements Runnable {
        private String officialFolder;
        private String fileName;

        public ThreadDeleteImageInOfficialFolder(String officialFolder, String fileName) {
            this.officialFolder = officialFolder;
            this.fileName = fileName;
        }

        @Override public void run() {
            String officialPath = getOfficialUploadPath(officialFolder);
            DeleteObjectRequest request = new DeleteObjectRequest(officialPath, fileName);
            s3.deleteObject(request);
        }
    }

}
