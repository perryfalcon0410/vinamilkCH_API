package vn.viettel.sale.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PO_AUTO_GROUP")
public class PoAutoGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "GROUP_NAME")
    private String groupName;
    @Column(name = "GROUP_CODE")
    private String groupCode;
    @Column(name = "PO_AUTO_GROUP_SHOP_MAP_ID")
    private Long poAutoGroupShopMapId;
    @Column(name = "STATUS")
    private Integer status;
}
