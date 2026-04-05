package bg.greencom.greencomwebapp.model.entity;

import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "internet_extras")
public class InternetExtrasEntity extends BaseEntity implements Comparable<InternetExtrasEntity>{

    private InternetExtraEnum name;

    public InternetExtrasEntity() {
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public InternetExtraEnum getName() {
        return name;
    }

    public InternetExtrasEntity setName(InternetExtraEnum name) {
        this.name = name;
        return this;
    }

    @Override
    public int compareTo(InternetExtrasEntity extra) {
        return this.name.compareTo(extra.getName());
    }
}
