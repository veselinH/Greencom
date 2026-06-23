package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * Resolves a Google (OIDC) login into the application's own principal.
 *
 * <p>Google is an OpenID Connect provider, so with the {@code openid} scope Spring Security uses the
 * OIDC flow — handled by {@code oidcUserService(...)}, not the plain-OAuth2
 * {@link CustomOAuth2UserService}. This service delegates to the standard {@link OidcUserService} to
 * fetch the verified ID token / user-info, then uses {@link OAuth2UserProvisioningService} to look up
 * / provision the local shadow {@link UserEntity}, returning a fully-populated
 * {@link GreencomUserDetails} (which implements {@code OidcUser}). Without this, the principal would
 * be a {@code DefaultOidcUser} and {@code @AuthenticationPrincipal GreencomUserDetails} would be null.
 */
@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OAuth2UserProvisioningService provisioningService;

    public CustomOidcUserService(OAuth2UserProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);

        UserEntity userEntity = provisioningService.provisionUser(
                oidcUser.getEmail(),
                oidcUser.getGivenName(),
                oidcUser.getFamilyName());

        return new GreencomUserDetails(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getEmail(),
                userEntity.getLastName(),
                provisioningService.toAuthorities(userEntity),
                oidcUser.getAttributes(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }
}
