package bg.greencom.greencomwebapp.model.entity;

import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRoleEntity extends BaseEntity{

    private UserRoleEnum name;

    public UserRoleEntity() {
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserRoleEnum getName() {
        return name;
    }

    public UserRoleEntity setName(UserRoleEnum name) {
        this.name = name;
        return this;
    }
}
