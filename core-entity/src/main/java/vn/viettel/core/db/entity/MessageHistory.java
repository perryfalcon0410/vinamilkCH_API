package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "message_history")
public class MessageHistory extends BaseEntity {

    @Column(name = "object", nullable = false)
    private Integer object;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "number_of_target")
    private Long numberOfTarget;

    public Integer getObject() {
        return object;
    }

    public void setObject(Integer object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getNumberOfTarget() {
        return numberOfTarget;
    }

    public void setNumberOfTarget(Long numberOfTarget) {
        this.numberOfTarget = numberOfTarget;
    }
}
