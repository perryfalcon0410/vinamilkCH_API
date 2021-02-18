package vn.viettel.core.dto;

import java.util.List;

public class SalonMemberResponseDTO {
    private Long memberId;
    private String salonIds;
    private List<Long> salonIdList;

    public SalonMemberResponseDTO() {
    }

    public SalonMemberResponseDTO(Long memberId, String salonIds, List<Long> salonIdList) {
        this.memberId = memberId;
        this.salonIds = salonIds;
        this.salonIdList = salonIdList;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getSalonIds() {
        return salonIds;
    }

    public void setSalonIds(String salonIds) {
        this.salonIds = salonIds;
    }

    public List<Long> getSalonIdList() {
        return salonIdList;
    }

    public void setSalonIdList(List<Long> salonIdList) {
        this.salonIdList = salonIdList;
    }
}
