package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "\"companies\"")
@AttributeOverride(name = "id", column = @Column(name = "company_id"))
public class Company2 extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private boolean status;

    @Column(name = "tel")
    private String tel;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "created_by")
    private Long createdBy;


}
