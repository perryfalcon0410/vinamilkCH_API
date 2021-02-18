package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "surveys")
@AttributeOverride(name = "id", column = @Column(name = "survey_id"))
public class Surveys extends BaseEntity {

    @Column(name = "management_user_id")
    private Long managementUserId;

    @Column(name = "title")
    private String title;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "url")
    private String url;

    public Surveys() {
    }

    public Surveys(Long managementUserId, String title, LocalDateTime createDate, String url) {
        this.managementUserId = managementUserId;
        this.title = title;
        this.createDate = createDate;
        this.url = url;
    }

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
