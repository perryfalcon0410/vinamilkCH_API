package vn.viettel.core.dto.survey;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyInfoResponseDTO {
    private Long surveyId;
    private Long managementUserId;
    private String title;
    private LocalDateTime createDate;
    private String url;
    private List<Long> surveyAppStores;
    private List<SurveyQuestionResponseDTO> questions;

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
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

    public List<Long> getSurveyAppStores() {
        return surveyAppStores;
    }

    public void setSurveyAppStores(List<Long> surveyAppStores) {
        this.surveyAppStores = surveyAppStores;
    }

    public List<SurveyQuestionResponseDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionResponseDTO> questions) {
        this.questions = questions;
    }
}
