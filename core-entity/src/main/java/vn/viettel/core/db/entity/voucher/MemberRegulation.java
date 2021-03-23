package vn.viettel.core.db.entity.voucher;

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
@Table(name = "MEMBER_REGULATION")
public class MemberRegulation extends BaseEntity {
    @Column(name = "MEMBER_CARD_ID")
    private Long memberCardId;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "NUM_DATE")
    private Integer numDate;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "STATUS")
    private Integer status;
}
