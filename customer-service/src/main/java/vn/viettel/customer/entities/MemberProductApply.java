package vn.viettel.customer.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MEMBER_PRODUCT_APPLY")
public class MemberProductApply extends BaseEntity {
    @Column(name = "MEMBER_CARD_ID")
    private Long memberCardId;
    @Column(name = "OBJECT_TYPE")
    private Integer objectType;
    @Column(name = "OBJECT_ID")
    private Long objectId;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "TO_DATE")
    private Date toDate;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "FACTOR")
    private Float factor;

}
