package vn.viettel.core.dto.waiting;

import java.time.LocalDate;
import java.util.List;

public class CustomerDetailInfoResponseDTO {
    private Long bookingId;

    private Long customerId;

    private Long memberId;

    private String memberQr;

    private String bookingCode;

    private String firstName;

    private String lastName;

    private String katakanaLastName;

    private String katakanaFirstName;

    private LocalDate birthday;

    private String tel;

    private String email;

    private Long salonId;

    private String salonName;

    private Long receptionId;

    private List<Long> familyStructure;

    private String familyStructureStr;

    private String zipcode;

    private String address;

    private String fullAddress;

    private Long prefectureId;

    private String prefecture;

    private String city;

    private Long cityId;

    private Long placeId;

    private String place;

    private LocalDate firstVisitDate;

    private LocalDate lastVisitDate;

    private Double point;

    private Long numberOfVisited;

    private Double totalPaymentCost;

    private Long numberPayment;

    private List<Long> knownSources;

    private String knownSourceStr;

    private List<Long> favoriteStyles;

    private String favoriteStyleStr;

    private List<Long> professions;

    private String professionStr;

    private List<Long> occupations;

    private String occupationStr;

    private List<Long> troubles;

    private String troubleStr;

    private String memo;

    private String lastMemo;

    public CustomerDetailInfoResponseDTO() {
    }

    public CustomerDetailInfoResponseDTO(Long bookingId, Long customerId, Long memberId, String memberQr, String bookingCode, String firstName, String lastName, String katakanaLastName, String katakanaFirstName, LocalDate birthday, String tel, String email, Long salonId, String salonName, Long receptionId, List<Long> familyStructure, String familyStructureStr, String zipcode, String address, String fullAddress, Long prefectureId, String prefecture, String city, Long cityId, Long placeId, String place, LocalDate firstVisitDate, LocalDate lastVisitDate, Double point, Long numberOfVisited, Double totalPaymentCost, Long numberPayment, List<Long> knownSources, String knownSourceStr, List<Long> favoriteStyles, String favoriteStyleStr, List<Long> professions, String professionStr, List<Long> occupations, String occupationStr, List<Long> troubles, String troubleStr, String memo, String lastMemo) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.memberId = memberId;
        this.memberQr = memberQr;
        this.bookingCode = bookingCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.katakanaLastName = katakanaLastName;
        this.katakanaFirstName = katakanaFirstName;
        this.birthday = birthday;
        this.tel = tel;
        this.email = email;
        this.salonId = salonId;
        this.salonName = salonName;
        this.receptionId = receptionId;
        this.familyStructure = familyStructure;
        this.familyStructureStr = familyStructureStr;
        this.zipcode = zipcode;
        this.address = address;
        this.fullAddress = fullAddress;
        this.prefectureId = prefectureId;
        this.prefecture = prefecture;
        this.city = city;
        this.cityId = cityId;
        this.placeId = placeId;
        this.place = place;
        this.firstVisitDate = firstVisitDate;
        this.lastVisitDate = lastVisitDate;
        this.point = point;
        this.numberOfVisited = numberOfVisited;
        this.totalPaymentCost = totalPaymentCost;
        this.numberPayment = numberPayment;
        this.knownSources = knownSources;
        this.knownSourceStr = knownSourceStr;
        this.favoriteStyles = favoriteStyles;
        this.favoriteStyleStr = favoriteStyleStr;
        this.professions = professions;
        this.professionStr = professionStr;
        this.occupations = occupations;
        this.occupationStr = occupationStr;
        this.troubles = troubles;
        this.troubleStr = troubleStr;
        this.memo = memo;
        this.lastMemo = lastMemo;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberQr() {
        return memberQr;
    }

    public void setMemberQr(String memberQr) {
        this.memberQr = memberQr;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public List<Long> getFamilyStructure() {
        return familyStructure;
    }

    public void setFamilyStructure(List<Long> familyStructure) {
        this.familyStructure = familyStructure;
    }

    public String getFamilyStructureStr() {
        return familyStructureStr;
    }

    public void setFamilyStructureStr(String familyStructureStr) {
        this.familyStructureStr = familyStructureStr;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(Long prefectureId) {
        this.prefectureId = prefectureId;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getFirstVisitDate() {
        return firstVisitDate;
    }

    public void setFirstVisitDate(LocalDate firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Long getNumberOfVisited() {
        return numberOfVisited;
    }

    public void setNumberOfVisited(Long numberOfVisited) {
        this.numberOfVisited = numberOfVisited;
    }

    public Double getTotalPaymentCost() {
        return totalPaymentCost;
    }

    public void setTotalPaymentCost(Double totalPaymentCost) {
        this.totalPaymentCost = totalPaymentCost;
    }

    public Long getNumberPayment() {
        return numberPayment;
    }

    public void setNumberPayment(Long numberPayment) {
        this.numberPayment = numberPayment;
    }

    public List<Long> getKnownSources() {
        return knownSources;
    }

    public void setKnownSources(List<Long> knownSources) {
        this.knownSources = knownSources;
    }

    public String getKnownSourceStr() {
        return knownSourceStr;
    }

    public void setKnownSourceStr(String knownSourceStr) {
        this.knownSourceStr = knownSourceStr;
    }

    public List<Long> getFavoriteStyles() {
        return favoriteStyles;
    }

    public void setFavoriteStyles(List<Long> favoriteStyles) {
        this.favoriteStyles = favoriteStyles;
    }

    public String getFavoriteStyleStr() {
        return favoriteStyleStr;
    }

    public void setFavoriteStyleStr(String favoriteStyleStr) {
        this.favoriteStyleStr = favoriteStyleStr;
    }

    public List<Long> getProfessions() {
        return professions;
    }

    public void setProfessions(List<Long> professions) {
        this.professions = professions;
    }

    public String getProfessionStr() {
        return professionStr;
    }

    public void setProfessionStr(String professionStr) {
        this.professionStr = professionStr;
    }

    public List<Long> getOccupations() {
        return occupations;
    }

    public void setOccupations(List<Long> occupations) {
        this.occupations = occupations;
    }

    public String getOccupationStr() {
        return occupationStr;
    }

    public void setOccupationStr(String occupationStr) {
        this.occupationStr = occupationStr;
    }

    public List<Long> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<Long> troubles) {
        this.troubles = troubles;
    }

    public String getTroubleStr() {
        return troubleStr;
    }

    public void setTroubleStr(String troubleStr) {
        this.troubleStr = troubleStr;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getLastMemo() {
        return lastMemo;
    }

    public void setLastMemo(String lastMemo) {
        this.lastMemo = lastMemo;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
