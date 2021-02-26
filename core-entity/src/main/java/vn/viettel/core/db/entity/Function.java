package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSoftOrder() {
        return softOrder;
    }

    public void setSoftOrder(int softOrder) {
        this.softOrder = softOrder;
    }
}
