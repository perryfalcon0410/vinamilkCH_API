package vn.viettel.core.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.StringUtils;
import org.hashids.Hashids;

import javax.imageio.ImageIO;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public final class GenerateUtils {

    public static String generateCode(String prefix, String separator, Long id, int codeLength) {
        int remainingLength = codeLength - id.toString().length();
        String remainingText = StringUtils.repeat("0", remainingLength);
        return new StringBuilder(prefix)
                .append(separator)
                .append(remainingText)
                .append(id)
                .toString();
    }

    public static String generateWebsiteUrl(String baseUrl, String code) {
        return new StringBuilder(baseUrl)
                .append("/")
                .append(code)
                .toString();
    }

    public static String generateUserID(String objectCode, String separator, Long id, int codeLength) {
        int remainingLength = codeLength - id.toString().length();
        String remainingText = StringUtils.repeat("0", remainingLength);
        return objectCode + separator + remainingText + id;
    }

    public static String hashidEncode(Long id) {
        Long start = 0l;
        Hashids hashids = new Hashids("", 10, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#`%^:*-+=[];,.");
        return hashids.encode(start + id);
    }

    public static String generateQRCode(Long id) {
        return String.format("%05d",id);
    }

    public static ByteArrayDataSource generateQRCodeToDataSource(String barcodeText) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);

        byte[] imageBytes = os.toByteArray();

        ByteArrayDataSource bds = new ByteArrayDataSource(imageBytes, "image/png");
        os.close();
        return bds;
    }

}
