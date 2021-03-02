
package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shops")
public class Shop extends BaseEntity {

    @Column(name = "shop_code")
    private String shopCode;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "shop_slug")
    private String shopSlug;

    @Column(name = "area")
    private String area;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mobiphone")
    private String mobiphone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private Integer status;

    @ApiModelProperty(notes = "latitude")
    @Column(name = "lat")
    private String lat;

    @ApiModelProperty(notes = "longitude")
    @Column(name = "lng")
    private String lng;

    @Column(name = "shop_type")
    private String shopType;

}
