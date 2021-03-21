package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PRODUCT_IMAGES")
public class ProductImage extends BaseEntity {

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "URL")
    private String url;
}