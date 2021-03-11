package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MEMBER_CARDS")
public class MemberCard extends BaseEntity{
    @Column(name = "CUSTOMER_ID")
    private long customerId;
    @Column(name = "CARD_TYPE")
    private int cardType;
    @Column(name = "GROUP_ID")
    private long groupId;


    public MemberCard(int cardType, long groupId, long createdBy) {
        this.cardType = cardType;
        this.groupId = groupId;

    }

}
