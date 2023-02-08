package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "data_mobile_plans")
public class DataPlanEntity extends MobilePlanEntity {

    public DataPlanEntity() {
    }
}
