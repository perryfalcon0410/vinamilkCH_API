package vn.viettel.core.service;

import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.util.ImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;

@Service
public class UploadImageServiceImpl extends UploadImageProcessor implements UploadImageService {

    private final static String[] WORDS_CONTAINS_IN_IMAGE_FIELD = {"picture", "photo", "logo", "favicon", "mediaurl", "data"};

    @Override
    public String uploadToTempFolderAndGetUrl(String imageData) {
        return uploadImageToTempFolderAndGetUrl(imageData);
    }

    @Override
    public <E extends BaseEntity> E updateEntityImageUrlWhenCreate(E entity, String officialFolder) {
        Field[] imageFields = getImageFields(entity);
        Arrays.stream(imageFields).parallel()
                .forEach(field -> field = changeTempImageUrlToOfficialImageUrlInField(entity, field, officialFolder));
        return entity;
    }

    @Override
    public <E extends BaseEntity> E updateEntityImageUrlWhenUpdate(E oldEntity, E newEntity, String officialFolder) {
        Field[] newImageFields = getImageFields(newEntity);
        Arrays.stream(newImageFields)
                .forEach(newImageField -> {
                    String newImageFieldName = newImageField.getName();
                    Field oldImageField = getImageFieldByName(oldEntity, newImageFieldName);

                    changeOldImageUrlToNewImageUrlInField(newEntity, oldEntity, newImageField, oldImageField, officialFolder);
                });
        return newEntity;
    }

    private <E extends BaseEntity> Field changeTempImageUrlToOfficialImageUrlInField(E entity, Field field, String officialFolder) {
        try {
            field.setAccessible(true);
            String url = (String) field.get(entity);
            if (StringUtils.isNotBlank(url)) {
                String fileName = ImageUtils.getFileNameByUrl(url);
                String officialImageUrl = asyncChangeTempImageIntoOfficialImageAndGetUrl(officialFolder, fileName);
                field.set(entity, officialImageUrl);
            }
        } catch (IllegalAccessException e) {

        }
        return field;
    }

    private <E extends BaseEntity> Field changeOldImageUrlToNewImageUrlInField(E newEntity, E oldEntity, Field newField, Field oldField, String officialFolder) {
        try {
            newField.setAccessible(true);
            oldField.setAccessible(true);

            String newUrl = (String) newField.get(newEntity);
            String oldUrl = (String) oldField.get(oldEntity);

            newUrl = StringUtils.defaultIfBlank(newUrl, StringUtils.EMPTY);
            oldUrl = StringUtils.defaultIfBlank(oldUrl, StringUtils.EMPTY);
            if (!newUrl.contains("TEMP") && (newUrl.contains("https://s3.ap-northeast-1.amazonaws.com/elizabeth-s3-access") || (newUrl.contains("https://d2c9g9x5pdn2i5.cloudfront.net")))) {
                newUrl = newUrl.replace("https://s3.ap-northeast-1.amazonaws.com/elizabeth-s3-access/", "")
                        .replace("https://d2c9g9x5pdn2i5.cloudfront.net/", "")
                        .replace("dev/", "").replace("stag/", "").replace("LIT/", "");
            }
            if (!newUrl.equals(oldUrl)) {
                if (StringUtils.isNotBlank(newUrl)) {
                    newField = changeTempImageUrlToOfficialImageUrlInField(newEntity, newField, officialFolder);
                }
                if (StringUtils.isNotBlank(oldUrl)) {
                    deleteOldImage(oldEntity, oldField, officialFolder);
                }
            } else if (!newUrl.equals("")) {
                newField.set(newEntity, newUrl);
            }
        } catch (IllegalAccessException e) {

        }
        return newField;
    }

    private <E extends BaseEntity> void deleteOldImage(E entity, Field field, String officialFolder) {
        try {
            field.setAccessible(true);
            String url = (String) field.get(entity);
            if (StringUtils.isNotBlank(url)) {
                String fileName = ImageUtils.getFileNameByUrl(url);
                asyncDeleteImageInOfficialFolder(officialFolder, fileName);
            }
        } catch (IllegalAccessException e) {

        }
    }

    private <E extends BaseEntity> Field[] getImageFields(E entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields()).parallel()
                .filter(this::isImageField)
                .toArray(Field[]::new);
    }

    private <E extends BaseEntity> Field getImageFieldByName(E entity, String fieldName) {
        Field field = null;
        try {
            field = entity.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {

        }
        return field;
    }

    private boolean isImageField(Field field) {
        String fieldName = field.getName().toLowerCase();
        boolean isFieldString = field.getType().isAssignableFrom(String.class);
        boolean isImageField = containsAnyInArray(fieldName, WORDS_CONTAINS_IN_IMAGE_FIELD);
        return isFieldString && isImageField;
    }

    private boolean containsAnyInArray(String s, String[] array) {
        s = StringUtils.defaultIfBlank(s, StringUtils.EMPTY);
        return Arrays.stream(array).parallel().anyMatch(s::contains);
    }
}
