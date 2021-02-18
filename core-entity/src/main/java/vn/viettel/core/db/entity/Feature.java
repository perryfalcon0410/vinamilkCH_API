package vn.viettel.core.db.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "features")
@AttributeOverride(name = "id", column = @Column(name = "feature_id"))
public class Feature extends BaseEntity {

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "is_shown", nullable = false)
    private boolean isShown;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "feature_id")
    private List<FeaturePlan> featurePlans = new ArrayList<FeaturePlan>();

    public Feature() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean isShown) {
        this.isShown = isShown;
    }

    public List<FeaturePlan> getFeaturePlans() {
        return featurePlans;
    }

    public void setFeaturePlans(List<FeaturePlan> featurePlans) {
        this.featurePlans = featurePlans;
    }

}
