package vn.viettel.core.dto.survey;

public class SurveyQuestionOptionResponseDTO {
    private Long surveyQuestionId;
    private Long surveyQuestionOptionId;
    private String content;

    public Long getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(Long surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public Long getSurveyQuestionOptionId() {
        return surveyQuestionOptionId;
    }

    public void setSurveyQuestionOptionId(Long surveyQuestionOptionId) {
        this.surveyQuestionOptionId = surveyQuestionOptionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
