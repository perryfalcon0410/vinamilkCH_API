package vn.viettel.core.dto.salon;

public class SalonStyleGroupDesignDetailResponseDTO {
    private Long salonId;

    private Long salonDesignId;

    private Long salonStyleGroupId;

    private Long salonStyleId;

    public SalonStyleGroupDesignDetailResponseDTO() {
    }

    public SalonStyleGroupDesignDetailResponseDTO(Long salonId, Long salonDesignId, Long salonStyleGroupId, Long salonStyleId) {
        this.salonId = salonId;
        this.salonDesignId = salonDesignId;
        this.salonStyleGroupId = salonStyleGroupId;
        this.salonStyleId = salonStyleId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getSalonDesignId() {
        return salonDesignId;
    }

    public void setSalonDesignId(Long salonDesignId) {
        this.salonDesignId = salonDesignId;
    }

    public Long getSalonStyleGroupId() {
        return salonStyleGroupId;
    }

    public void setSalonStyleGroupId(Long salonStyleGroupId) {
        this.salonStyleGroupId = salonStyleGroupId;
    }

    public Long getSalonStyleId() {
        return salonStyleId;
    }

    public void setSalonStyleId(Long salonStyleId) {
        this.salonStyleId = salonStyleId;
    }
}
