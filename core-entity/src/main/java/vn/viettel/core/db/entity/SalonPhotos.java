package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_photos")
@AttributeOverride(name = "id", column = @Column(name = "salon_photos_id"))
public class SalonPhotos extends BaseEntity {
    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "photo_type")
    private Long photoType;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getPhotoType() {
        return photoType;
    }

    public void setPhotoType(Long photoType) {
        this.photoType = photoType;
    }
}
