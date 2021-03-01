package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "survey_details")
@AttributeOverride(name = "id", column = @Column(name = "survey_detail_id"))
public class SurveyDetails extends BaseEntity {

    @Column(name = "survey_id")
    private Long surveyId;

    @Column(name = "salon_id")
    private Long salonId;

    public SurveyDetails() {
    }

    public SurveyDetails(Long surveyId, Long salonId) {
        this.surveyId = surveyId;
        this.salonId = salonId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }
}
