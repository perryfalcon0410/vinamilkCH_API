package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "survey_question_options")
@AttributeOverride(name = "id", column = @Column(name = "survey_question_option_id"))
public class SurveyQuestionOptions extends BaseEntity {

    @Column(name = "survey_question_id")
    private Long surveyQuestionId;

    @Column(name = "content")
    private String content;

    public SurveyQuestionOptions() {
    }

    public SurveyQuestionOptions(Long surveyQuestionId, String content) {
        this.surveyQuestionId = surveyQuestionId;
        this.content = content;
    }

    public Long getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(Long surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
