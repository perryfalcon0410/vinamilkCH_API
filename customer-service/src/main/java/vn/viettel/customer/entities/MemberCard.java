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
@Table(name = "MEMBER_CARD")
public class MemberCard extends BaseEntity {
    @Column(name = "MEMBER_CARD_CODE")
    private String memberCardCode;
    @Column(name = "MEMBER_CARD_NAME")
    private String memberCardName;
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "LEVEL_CARD")
    private Integer levelCard;


}
