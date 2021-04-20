package vn.viettel.core.db.entity.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "AREAS")
public class Area extends BaseEntity {
    @Column(name = "AREA_CODE")
    private String areaCode;
    @Column(name = "AREA_NAME")
    private String areaName;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "PARENT_AREA_ID")
    private Long parentAreaId;
    @Column(name = "PROVINCE")
    private String province;
    @Column(name = "PROVINCE_NAME")
    private String provinceName;
    @Column(name = "DISTRICT")
    private String district;
    @Column(name = "DISTRICT_NAME")
    private String districtName;
    @Column(name = "PRECINCT")
    private String precinct;
    @Column(name = "PRECINCT_NAME")
    private String precinctName;
    @Column(name = "PARENT_CODE")
    private String parentCode;
    @Column(name = "STATUS")
    private Integer status;
}
