package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reception_logs")
@AttributeOverride(name = "id", column = @Column(name = "reception_log_id"))
public class ReceptionLog extends BaseEntity {

    @Column(name = "reception_id", nullable = false)
    private Long receptionId;

    @Column(name = "history")
    private String history;

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
