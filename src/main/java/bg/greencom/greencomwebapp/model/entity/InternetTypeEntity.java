package bg.greencom.greencomwebapp.model.entity;

import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "internet_types")
public class InternetTypeEntity extends BaseEntity{

    private InternetTypeEnum name;

    public InternetTypeEntity() {
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public InternetTypeEnum getName() {
        return name;
    }

    public InternetTypeEntity setName(InternetTypeEnum name) {
        this.name = name;
        return this;
    }
}
