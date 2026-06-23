package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.UserRoleService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shared provisioning logic for federated logins (OAuth2 and OIDC).
 *
 * <p>Looks up the local {@link UserEntity} by email, provisioning a "shadow" account on first
 * login, and maps roles to {@code ROLE_*} granted authorities. Both {@link CustomOAuth2UserService}
 * (plain OAuth2 providers, e.g. GitHub/Facebook) and {@link CustomOidcUserService} (OIDC providers,
 * e.g. Google) depend on this component, so neither user service depends on the other.
 */
@Service
public class OAuth2UserProvisioningService {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    public OAuth2UserProvisioningService(@Lazy UserRepository userRepository,
                                         @Lazy UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    /**
     * Returns the existing local account for {@code email}, or creates and persists a new shadow
     * account (username = email, {@code ROLE_USER}, empty native password) on first login.
     */
    public UserEntity provisionUser(String email, String firstName, String lastName) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email)
                    .setUsername(email)
                    .setFirstName(firstName != null ? firstName : "Unknown")
                    .setLastName(lastName != null ? lastName : "User")
                    .setTotalDebtPerMonth(BigDecimal.ZERO)
                    .setRegisteredOn(LocalDateTime.now())
                    .setPassword(""); // Federated accounts have no native password

            newUser.getRoles().add(userRoleService.findByName(UserRoleEnum.USER));
            return userRepository.save(newUser);
        });
    }

    /** Maps the user's roles to {@code ROLE_*} granted authorities. */
    public List<GrantedAuthority> toAuthorities(UserEntity userEntity) {
        return userEntity.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toList());
    }
}
