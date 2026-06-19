package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;

/**
 * Manages user roles ({@code ADMIN}, {@code USER}).
 */
public interface UserRoleService {

    /** Seeds all {@code UserRoleEnum} values into the database if not already present. */
    void initialize();

    /** Returns the role entity for the given role enum value. */
    UserRoleEntity findByName(UserRoleEnum userRoleEnum);
}
