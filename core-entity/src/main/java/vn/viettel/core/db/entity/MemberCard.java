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
@Table(name = "MEMBER_CARDS")
public class MemberCard extends BaseEntity {
    @Column(name = "MEMBER_CARD_CODE")
    private String memberCardCode;
    @Column(name = "MEMBER_CARD_TYPE_ID")
    private Long memberCardTypeId;
    @Column(name = "MEMBER_CARD_CUSTOMER_TYPE_ID")
    private Long memberCardCustomerTypeId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "DELETED_BY")
    private Long deletedBy;

}
