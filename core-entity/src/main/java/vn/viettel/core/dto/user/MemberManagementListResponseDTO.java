package vn.viettel.core.dto.user;

import vn.viettel.core.annotation.Scalar;
import vn.viettel.core.db.entity.commonEnum.CustomJPAType;

import java.time.LocalDate;

public class MemberManagementListResponseDTO {

    @Scalar(type = CustomJPAType.LONG)
    private Long id;

    @Scalar(type = CustomJPAType.STRING)
    private String idStr;

    @Scalar(type = CustomJPAType.LONG)
    private Long memberTypeId;

    @Scalar(type = CustomJPAType.STRING)
    private String memberTypeName;

    @Scalar(type = CustomJPAType.STRING)
    private String firstName;

    @Scalar(type = CustomJPAType.STRING)
    private String lastName;

    @Scalar(type = CustomJPAType.STRING)
    private String katakanaLastName;

    @Scalar(type = CustomJPAType.STRING)
    private String katakanaFirstName;

    @Scalar(type = CustomJPAType.BYTE)
    private Byte gender;

    @Scalar(type = CustomJPAType.STRING)
    private String genderStr;

    private LocalDate firstVisitedDate;

    private LocalDate lastVisitedDate;

    private Long totalVisited;

    private Long totalTimeWaiting;

    @Scalar(type = CustomJPAType.DOUBLE)
    private Double point;

    public MemberManagementListResponseDTO() {
    }

    public MemberManagementListResponseDTO(Long id, String idStr) {
        this.id = id;
        this.idStr = idStr;
    }

    public MemberManagementListResponseDTO(Long id, String idStr, Long memberTypeId, String memberTypeName, String firstName, String lastName, String katakanaLastName, String katakanaFirstName, Byte gender, String genderStr, LocalDate firstVisitedDate, LocalDate lastVisitedDate, Long totalVisited, Long totalTimeWaiting, Double point) {
        this.id = id;
        this.idStr = idStr;
        this.memberTypeId = memberTypeId;
        this.memberTypeName = memberTypeName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.katakanaLastName = katakanaLastName;
        this.katakanaFirstName = katakanaFirstName;
        this.gender = gender;
        this.genderStr = genderStr;
        this.firstVisitedDate = firstVisitedDate;
        this.lastVisitedDate = lastVisitedDate;
        this.totalVisited = totalVisited;
        this.totalTimeWaiting = totalTimeWaiting;
        this.point = point;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getKatakanaLastName() {
        return katakanaLastName;
    }

    public void setKatakanaLastName(String katakanaLastName) {
        this.katakanaLastName = katakanaLastName;
    }

    public String getKatakanaFirstName() {
        return katakanaFirstName;
    }

    public void setKatakanaFirstName(String katakanaFirstName) {
        this.katakanaFirstName = katakanaFirstName;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public LocalDate getFirstVisitedDate() {
        return firstVisitedDate;
    }

    public void setFirstVisitedDate(LocalDate firstVisitedDate) {
        this.firstVisitedDate = firstVisitedDate;
    }

    public LocalDate getLastVisitedDate() {
        return lastVisitedDate;
    }

    public void setLastVisitedDate(LocalDate lastVisitedDate) {
        this.lastVisitedDate = lastVisitedDate;
    }

    public Long getTotalVisited() {
        return totalVisited;
    }

    public void setTotalVisited(Long totalVisited) {
        this.totalVisited = totalVisited;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Long getTotalTimeWaiting() {
        return totalTimeWaiting;
    }

    public void setTotalTimeWaiting(Long totalTimeWaiting) {
        this.totalTimeWaiting = totalTimeWaiting;
    }

    public String getGenderStr() {
        return genderStr;
    }

    public void setGenderStr(String genderStr) {
        this.genderStr = genderStr;
    }
}
