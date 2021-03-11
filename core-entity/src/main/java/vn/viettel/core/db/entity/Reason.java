package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "REASONS")
public class Reason extends BaseEntity{

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "REASON_NAME")
    private  String reasonName;

    @Column(name = "STATUS")
    private  Integer status;

    @Column(name = "CREATE_BY")
    private Long createdBy;

    @Column(name = "UPDATE_BY")
    private Long updatedBy;

    @Column(name = "DELETE_BY")
    private Long deletedBy;
}
