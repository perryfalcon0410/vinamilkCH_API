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
    private long countryId;
    @Column(name = "AREA_ID")
    private long areaId;
    @Column(name = "PROVINCE_ID")
    private long provinceId;
    @Column(name = "DISTRICT_ID")
    private long districtId;
    @Column(name = "WARD_ID")
    private long wardId;
    @Column(name = "ADDRESS_ID")
    private long addressId;

    public FullAddress(long countryId, long areaId, long provinceId, long districtId, long wardId, long addressId) {
        this.countryId = countryId;
        this.areaId = areaId;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardId = wardId;
        this.addressId = addressId;
    }
}




