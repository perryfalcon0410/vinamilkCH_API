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
@Table(name = "functions")
public class Function extends BaseEntity{
    @Column(name = "name")
    private String name;
    @Column(name = "url")
    private String url;
    @Column(name = "parent_id")
    private int parentId;
    @Column(name = "status")
    private int status;
    @Column(name = "soft_order")
    private int softOrder;
}
