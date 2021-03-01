package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "booking_hair_photos")
@AttributeOverride(name = "id", column = @Column(name = "booking_hair_photo_id"))
public class BookingHairPhoto extends BaseEntity {

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Column(name = "photo_type")
    private Long photoType;

    @Column(name = "photo_type_name")
    private String photoTypeName;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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

    public String getPhotoTypeName() {
        return photoTypeName;
    }

    public void setPhotoTypeName(String photoTypeName) {
        this.photoTypeName = photoTypeName;
    }
}
