package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_image")
public class ProductImage extends BaseEntity {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_url")
    private String productUrl;

    @Column(name = "status")
    private Integer status;


}