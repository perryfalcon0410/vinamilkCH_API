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
@Table(name = "ID_CARDS")
public class IDCard extends BaseEntity{
    @Column(name = "CUS_ID")
    private long cusId;
    @Column(name = "ID_NO")
    private String idNumber;
    @Column(name = "ISSUE_DATE")
    private Date issueDate;
    @Column(name = "ISSUE_PLACE")
    private String issuePlace;

    public IDCard(String idNumber, Date issueDate, String issuePlace) {
        this.idNumber = idNumber;
        this.issueDate = issueDate;
        this.issuePlace = issuePlace;
    }

}
