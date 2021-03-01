package vn.viettel.core.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "id_cards")
public class IDCard extends BaseEntity{
    private long cusId;
    @Column(name = "id_no")
    private String idNumber;
    @Column(name = "issue_date")
    private Date issueDate;
    @Column(name = "issue_place")
    private String issuePlace;

    public IDCard(String idNumber, Date issueDate, String issuePlace) {
        this.idNumber = idNumber;
        this.issueDate = issueDate;
        this.issuePlace = issuePlace;
    }

}
