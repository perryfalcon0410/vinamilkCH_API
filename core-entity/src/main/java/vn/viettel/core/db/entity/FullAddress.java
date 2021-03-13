package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "FULL_ADDRESS")
public class FullAddress extends BaseEntity{
    @Column(name = "COUNTRY_ID")
    private Long countryId;
    @Column(name = "AREA_ID")
    private Long areaId;
    @Column(name = "PROVINCE_ID")
    private Long provinceId;
    @Column(name = "DISTRICT_ID")
    private Long districtId;
    @Column(name = "WARD_ID")
    private Long wardId;
    @Column(name = "ADDRESS_ID")
    private Long addressId;

    public FullAddress(Long countryId, Long areaId, Long provinceId, Long districtId, Long wardId, Long addressId) {
        this.countryId = countryId;
        this.areaId = areaId;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardId = wardId;
        this.addressId = addressId;
    }
}




