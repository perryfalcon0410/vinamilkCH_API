package vn.viettel.customer.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MEMBER_SCORE_CONVERT")
public class MemberScoreConvert extends BaseEntity {
    @Column(name = "MEMBER_CARD_ID")
    private Long memberCardId;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "SCORE")
    private Integer score;
    @Column(name = "FROM_DATE")
    private LocalDateTime fromDate;
    @Column(name = "TO_DATE")
    private LocalDateTime toDate;
    @Column(name = "STATUS")
    private Integer status;

}
