package vn.viettel.core.service;

import vn.viettel.core.db.entity.BaseEntity;

public interface UploadImageService {

    String uploadToTempFolderAndGetUrl(String imageData);

    <E extends BaseEntity> E updateEntityImageUrlWhenCreate(E entity, String officialFolder);

    <E extends BaseEntity> E updateEntityImageUrlWhenUpdate(E oldEntity, E newEntity, String officialFolder);
}
