package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "member_card")
public class MemberCard extends BaseEntity{
    @Column(name = "customer_id")
    private long customerId;
    @Column(name = "card_type")
    private int cardType;
    @Column(name = "group_id")
    private long groupId;
    @Column(name = "created_by")
    private long createdBy;
    @Column(name = "updated_by")
    private long updatedBy;
    @Column(name = "deleted_by")
    private long deletedBy;

    public MemberCard(int cardType, long groupId, long createdBy) {
        this.cardType = cardType;
        this.groupId = groupId;
        this.createdBy = createdBy;
    }

}
