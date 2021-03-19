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
@Table(name = "COMBO_PRODUCTS")
public class ComboProduct extends BaseEntity{
    @Column(name = "COMBO_PRODUCT_CODE")
    private String comboProductCode;
    @Column(name = "COMBO_PRODUCT_NAME")
    private String comboProductName;
    @Column(name = "NUM_PRODUCT")
    private Integer numProduct;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "STATUS")
    private Float status;
}
