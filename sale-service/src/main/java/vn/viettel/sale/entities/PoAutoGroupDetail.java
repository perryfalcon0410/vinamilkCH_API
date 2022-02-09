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
@Table(name = "PO_AUTO_GROUP_DETAIL")
public class PoAutoGroupDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PO_AUTO_GROUP_ID")
    private Long poAutoGroupoId;
    @Column(name = "OBJECT_TYPE")
    private Integer objectType;
    @Column(name = "OBJECT_ID")
    private Long objectId;
    @Column(name = "STATUS")
    private Integer status;
}
