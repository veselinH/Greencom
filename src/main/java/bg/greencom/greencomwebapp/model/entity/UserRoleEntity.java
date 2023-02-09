package bg.greencom.greencomwebapp.model.entity;

import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "user_roles")
public class UserRoleEntity extends BaseEntity {

    private UserRoleEnum name;
    private Set<UserEntity> usersEntities;

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

    @ManyToMany(mappedBy = "roles")
    public Set<UserEntity> getUsersEntities() {
        return usersEntities;
    }

    public UserRoleEntity setUsersEntities(Set<UserEntity> usersEntities) {
        this.usersEntities = usersEntities;
        return this;
    }
}
