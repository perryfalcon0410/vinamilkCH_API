package vn.viettel.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class ImageUtils {

    private final static String[] ICON_EXTENSIONS = {"x-icon", "png"};

    /* GET CONTENT TYPE */
    public static String getContentType(String image) {
        return StringUtils.substringBetween(image, "data:", ";");
    }

    /* GET FILE EXTENSION */
    public static String getFileExtension(String image) {
        String contentType = getContentType(image);
        return StringUtils.substringAfter(contentType, "/");
    }

    /* GET BASE64 */
    public static String getBase64(String image) {
        return StringUtils.substringAfter(image, "base64,");
    }

    /* GET FILE NAME BY URL */
    public static String getFileNameByUrl(String url) {
        return StringUtils.substringAfterLast(url, "/");
    }

    public static boolean isNotIconByUrl(String url) {
        boolean isNotIcon = true;
        if (StringUtils.isNotBlank(url)) {
            String separator = ".";
            String ext = StringUtils.substringAfterLast(url, separator).toLowerCase();
            isNotIcon = Arrays.stream(ICON_EXTENSIONS).noneMatch(ext::equals);
        }
        return isNotIcon;
    }

}
