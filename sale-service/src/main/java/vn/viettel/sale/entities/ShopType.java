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
@Table(name = "SHOP_TYPE")
public class ShopType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "OBJECT_TYPE")
    private Integer objectType;
    @Column(name = "STATUS")
    private Integer status;
}
