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
@Table(name = "reason")
public class Reason extends BaseEntity{

    @Column(name = "reason_code")
    private String reasonCode;

    @Column(name = "reason_name")
    private  String reasonName;

    @Column(name = "status")
    private  Integer status;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_by")
    private Long deletedBy;
}
