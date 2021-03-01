package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;
import java.util.List;

public class SalonMenuTagsResponseDTO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private String salonMenuName;

    private Long salonMenuId;

    private List<String> salonTagNameList;

    public SalonMenuTagsResponseDTO() {
    }

    public SalonMenuTagsResponseDTO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, String salonMenuName, Long salonMenuId, List<String> salonTagNameList) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.salonMenuName = salonMenuName;
        this.salonMenuId = salonMenuId;
        this.salonTagNameList = salonTagNameList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getSalonMenuName() {
        return salonMenuName;
    }

    public void setSalonMenuName(String salonMenuName) {
        this.salonMenuName = salonMenuName;
    }

    public Long getSalonMenuId() {
        return salonMenuId;
    }

    public void setSalonMenuId(Long salonMenuId) {
        this.salonMenuId = salonMenuId;
    }

    public List<String> getSalonTagNameList() {
        return salonTagNameList;
    }

    public void setSalonTagNameList(List<String> salonTagNameList) {
        this.salonTagNameList = salonTagNameList;
    }
}
