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
@Table(name = "FUNCTIONS")
public class Function extends BaseEntity{
    @Column(name = "NAME")
    private String name;
    @Column(name = "URL")
    private String url;
    @Column(name = "PARENT_ID")
    private int parentId;
    @Column(name = "STATUS")
    private int status;
    @Column(name = "SOFT_ORDER")
    private int softOrder;
}
