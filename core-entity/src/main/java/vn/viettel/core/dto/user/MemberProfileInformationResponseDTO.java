package vn.viettel.core.dto.user;

import java.time.LocalDate;
import java.util.List;

public class MemberProfileInformationResponseDTO {
    private Long memberId;

    private String memberQr;

    private String firstName;

    private String lastName;

    private String katakanaLastName;

    private String katakanaFirstName;

    private LocalDate birthday;

    private String tel;

    private String email;

    private Integer longEPassword;

    private Long salonId;

    private String salonName;

    private String salonSlug;

    private String zipcode;

    private String address;

    private String initialAddress;

    private String fullAddress;

    private Long prefectureId;

    private Long cityId;

    private Long placeId;

    private String prefecture;

    private String city;

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

    private List<Long> familyStructure;

    private String familyStructureStr;

    private List<ChannelTypeResponseDTO> channels;

    private Byte gender;

    private Double point;

    public MemberProfileInformationResponseDTO() {
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

    public Integer getLongEPassword() {
        return longEPassword;
    }

    public void setLongEPassword(Integer longEPassword) {
        this.longEPassword = longEPassword;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<ChannelTypeResponseDTO> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelTypeResponseDTO> channels) {
        this.channels = channels;
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

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public String getInitialAddress() {
        return initialAddress;
    }

    public void setInitialAddress(String initialAddress) {
        this.initialAddress = initialAddress;
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
}
