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
@Table(name = "so_confirm")
public class SOConfirm extends BaseEntity{
    @Column(name = "po_no")
    private String poNo;

    @Column(name = "so_no")
    private String soNo;
}
