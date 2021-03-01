package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "survey_questions")
@AttributeOverride(name = "id", column = @Column(name = "survey_question_id"))
public class SurveyQuestions extends BaseEntity {

    @Column(name = "survey_id")
    private Long surveyId;

    @Column(name = "content")
    private String content;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "type")
    private Byte type;

    public SurveyQuestions() {
    }

    public SurveyQuestions(Long surveyId, String content, Boolean required, Byte type) {
        this.surveyId = surveyId;
        this.content = content;
        this.required = required;
        this.type = type;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}
