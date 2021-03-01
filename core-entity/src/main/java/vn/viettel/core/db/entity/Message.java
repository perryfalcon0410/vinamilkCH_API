package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message extends BaseEntity {

    @Column(name = "object", nullable = false)
    private Integer object;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "message_title", nullable = false)
    private String messageTitle;

    @Column(name = "type")
    private String type;

    @Column(name = "email_type")
    private String emailType;

    @Column(name = "target")
    private String target;

    @Column(name = "text_mail")
    private String textMail;

    @Column(name = "content_1")
    private String content1;

    @Column(name = "image_picture_url")
    private String imagePictureUrl;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "content_2")
    private String content2;

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

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTextMail() {
        return textMail;
    }

    public void setTextMail(String textMail) {
        this.textMail = textMail;
    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getImagePictureUrl() {
        return imagePictureUrl;
    }

    public void setImagePictureUrl(String imagePictureUrl) {
        this.imagePictureUrl = imagePictureUrl;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }
}
