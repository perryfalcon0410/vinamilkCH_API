package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "\"groups\"")
public class Group2 extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_code", length = 12)
    private String groupCode;

    @Column(name = "name")
    private String name;

    @Column(name = "name_kana")
    private String nameKana;

    @Column(name = "group_type_id")
    private Long groupTypeId;

    @Column(name = "officer_name")
    private String officerName;

    @Column(name = "open_status")
    private boolean openStatus;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "tel", length = 16)
    private String tel;

    @Column(name = "fax", length = 16)
    private String fax;

    @Column(name = "has_parking_lot")
    private boolean hasParkingLot;

    @Column(name = "business_day")
    private String businessDay;

    @Column(name = "nearest_station")
    private String nearestStation;

    @Column(name = "email")
    private String email;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "picture1_url")
    private String picture1Url;

    @Column(name = "picture2_url")
    private String picture2Url;

    @Column(name = "picture3_url")
    private String picture3Url;

    @Column(name = "picture4_url")
    private String picture4Url;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "line_url")
    private String lineUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    @Column(name = "is_publish")
    private boolean publish;

    @Column(name = "payjp_customer_id", length = 64)
    private String payjpCustomerId;

    @Column(name = "security_policy")
    private String securityPolicy;

    @Column(name = "distributor_id")
    private Long distributorId;

    @Column(name = "allow_distributor_login")
    private Boolean allowDistributorLogin;

}
