package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "data_mobile_plans")
public class DataPlanEntity extends MobilePlanEntity {
    public DataPlanEntity() {
    }

    @Override
    @Transient
    public String getPlanType() {
        return "Mobile Data";
    }
}
