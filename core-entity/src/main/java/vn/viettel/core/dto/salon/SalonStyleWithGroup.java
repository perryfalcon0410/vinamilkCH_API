package vn.viettel.core.dto.salon;

import org.springframework.util.CollectionUtils;

import java.util.List;

public class SalonStyleWithGroup {
    private Long salonStyleGroupId;

    private String salonStyleGroupName;

    private List<SalonStyleWithTag> styles;

    public SalonStyleWithGroup() {
        //styles = new ArrayList<>();
    }

    public SalonStyleWithGroup(Long salonStyleGroupId, String salonStyleGroupName, List<SalonStyleWithTag> styles) {
        this.salonStyleGroupId = salonStyleGroupId;
        this.salonStyleGroupName = salonStyleGroupName;
        this.styles = styles;
    }

    public Long getSalonStyleGroupId() {
        return salonStyleGroupId;
    }

    public void setSalonStyleGroupId(Long salonStyleGroupId) {
        this.salonStyleGroupId = salonStyleGroupId;
    }

    public String getSalonStyleGroupName() {
        return salonStyleGroupName;
    }

    public void setSalonStyleGroupName(String salonStyleGroupName) {
        this.salonStyleGroupName = salonStyleGroupName;
    }

    public List<SalonStyleWithTag> getStyles() {
        return styles;
    }

    public void setStyles(List<SalonStyleWithTag> styles) {
        this.styles = styles;
    }

    public boolean isPracticalNull() {
        return (CollectionUtils.isEmpty(styles) || (styles.size()==1 && styles.get(0).isPracticalNull()))
                        && this.salonStyleGroupId == null && this.salonStyleGroupName == null;
    }
}
