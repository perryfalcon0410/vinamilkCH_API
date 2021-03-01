package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.NotificationContentType;
import vn.viettel.core.db.entity.status.NotificationMediaActionType;
import vn.viettel.core.db.entity.status.converter.NotificationContentTypeConverter;
import vn.viettel.core.db.entity.status.converter.NotificationMediaActionTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_notifications")
public class GroupNotification extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "title")
    private String title;

    @Column(name = "enable", nullable = false)
    private boolean enable;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "content_type", nullable = false)
    @Convert(converter = NotificationContentTypeConverter.class)
    private NotificationContentType contentType;

    @Column(name = "pin_to_home_page", nullable = false)
    private boolean pinToHomePage;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "media_action_type")
    @Convert(converter = NotificationMediaActionTypeConverter.class)
    private NotificationMediaActionType mediaActionType;

    @Column(name = "media_action_url")
    private String mediaActionUrl;

    @Column(name = "content")
    private String content;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public NotificationContentType getContentType() {
        return contentType;
    }

    public void setContentType(NotificationContentType contentType) {
        this.contentType = contentType;
    }

    public boolean isPinToHomePage() {
        return pinToHomePage;
    }

    public void setPinToHomePage(boolean pinToHomePage) {
        this.pinToHomePage = pinToHomePage;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public NotificationMediaActionType getMediaActionType() {
        return mediaActionType;
    }

    public void setMediaActionType(NotificationMediaActionType mediaActionType) {
        this.mediaActionType = mediaActionType;
    }

    public String getMediaActionUrl() {
        return mediaActionUrl;
    }

    public void setMediaActionUrl(String mediaActionUrl) {
        this.mediaActionUrl = mediaActionUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
