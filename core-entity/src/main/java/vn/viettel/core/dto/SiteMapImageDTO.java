package vn.viettel.core.dto;

import java.util.List;

public class SiteMapImageDTO {

    private Long id;

    private String topPageLink;

    private List<String> imageLinkList;

    private String objectName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopPageLink() {
        return topPageLink;
    }

    public void setTopPageLink(String topPageLink) {
        this.topPageLink = topPageLink;
    }

    public List<String> getImageLinkList() {
        return imageLinkList;
    }

    public void setImageLinkList(List<String> imageLinkList) {
        this.imageLinkList = imageLinkList;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
