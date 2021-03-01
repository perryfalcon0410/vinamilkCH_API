package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "full_address")
public class FullAddress extends BaseEntity{
    @Column(name = "country_id")
    private long countryId;
    @Column(name = "area_id")
    private long areaId;
    @Column(name = "province_id")
    private long provinceId;
    @Column(name = "district_id")
    private long districtId;
    @Column(name = "ward_id")
    private long wardId;
    @Column(name = "address_id")
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




