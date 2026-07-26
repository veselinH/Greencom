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

@Service
public class OAuth2UserProvisioningService {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    public OAuth2UserProvisioningService(@Lazy UserRepository userRepository,
                                         @Lazy UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    public UserEntity provisionUser(String email, String firstName, String lastName) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email)
                    .setUsername(email)
                    .setFirstName(firstName != null ? firstName : "Unknown")
                    .setLastName(lastName != null ? lastName : "User")
                    .setTotalDebtPerMonth(BigDecimal.ZERO)
                    .setRegisteredOn(LocalDateTime.now())
                    .setPassword("");

            newUser.getRoles().add(userRoleService.findByName(UserRoleEnum.USER));
            return userRepository.save(newUser);
        });
    }

    public List<GrantedAuthority> toAuthorities(UserEntity userEntity) {
        return userEntity.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toList());
    }
}
