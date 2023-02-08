package bg.greencom.greencomwebapp.model.entity;

import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "mobile_extras")
public class MobileExtraEntity extends BaseEntity {

    private MobileExtraEnum name;

    public MobileExtraEntity() {
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MobileExtraEnum getName() {
        return name;
    }

    public MobileExtraEntity setName(MobileExtraEnum name) {
        this.name = name;
        return this;
    }
}
