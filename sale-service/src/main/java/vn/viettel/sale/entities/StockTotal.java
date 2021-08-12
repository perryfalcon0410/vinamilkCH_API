package vn.viettel.sale.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_TOTAL")
@EntityListeners(AuditingEntityListener.class)
public class StockTotal extends BaseEntity/* extends BaseEntity*/ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "WARE_HOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "STATUS")
    private Integer status;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @LastModifiedDate
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "UPDATED_BY")
    private String updatedBy;
}
