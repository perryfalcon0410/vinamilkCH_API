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


    public MemberCard(int cardType, long groupId, long createdBy) {
        this.cardType = cardType;
        this.groupId = groupId;

    }

}
