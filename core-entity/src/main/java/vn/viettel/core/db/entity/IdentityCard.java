package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "IDENTITY_CARDS")
public class IdentityCard extends BaseEntity {
    @Column(name = "IDENTITY_CARD_CODE")
    private String identityCardCode;
    @Column(name = "IDENTITY_CARD_ISSUE_DATE")
    private Date identityCardIssueDate;
    @Column(name = "IDENTITY_CARD_ISSUE_PLACE")
    private String identityCardIssuePlace;
    @Column(name = "IDENTITY_CARD_EXPIRY_DATE")
    private Date identityCardExpiryDate;
    @Column(name = "IDENTITY_CARD_TYPE")
    private Long identityCardType;
}
