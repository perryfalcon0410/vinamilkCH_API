package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMBO_PRODUCT")
public class ComboProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "PRODUCT_NAME_TEXT")
    private String productNameText;
    @Column(name = "REF_PRODUCT_ID")
    private Long refProductId;
    @Column(name = "NUM_PRODUCT")
    private Integer numProduct;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "STATUS")
    private Integer status;
}
