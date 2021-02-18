package vn.viettel.core.dto.survey;

import java.time.LocalDateTime;

public class SurveyListResponseDTO {
    private Long surveyId;
    private String title;
    private LocalDateTime createDate;
    private String author;
    private Long appStores;
    private String url;

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getAppStores() {
        return appStores;
    }

    public void setAppStores(Long appStores) {
        this.appStores = appStores;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
