package bg.greencom.greencomwebapp.model.entity;

import bg.greencom.greencomwebapp.model.entity.enums.TelevisionTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "television_types")
public class TelevisionTypeEntity extends BaseEntity {

    private TelevisionTypeEnum name;

    public TelevisionTypeEntity() {
    }

    @Enumerated(EnumType.STRING)
    public TelevisionTypeEnum getName() {
        return name;
    }

    public TelevisionTypeEntity setName(TelevisionTypeEnum name) {
        this.name = name;
        return this;
    }
}
