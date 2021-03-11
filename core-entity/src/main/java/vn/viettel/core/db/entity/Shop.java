
package vn.viettel.core.db.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SHOPS")
public class Shop extends BaseEntity {

    @Column(name = "SHOP_CODE")
    private String shopCode;

    @Column(name = "SHOP_NAME")
    private String shopName;

    @Column(name = "SHOP_SLUG")
    private String shopSlug;

    @Column(name = "FULL_ADDRESS_ID")
    private String area;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;

    @Column(name = "FAX")
    private String fax;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "STATUS")
    private Integer status;

    @ApiModelProperty(notes = "latitude")
    @Column(name = "LAT")
    private String lat;

    @ApiModelProperty(notes = "longitude")
    @Column(name = "LNG")
    private String lng;

    @Column(name = "SHOP_TYPE")
    private Integer shopType;

}
