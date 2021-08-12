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
@Table(name = "PRODUCT_INFO")
public class ProductInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PRODUCT_INFO_CODE")
    private String productInfoCode;
    @Column(name = "PRODUCT_INFO_NAME")
    private String productInfoName;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "STATUS")
    private Integer status;
}
