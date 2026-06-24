package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Resolves a plain-OAuth2 login (non-OIDC providers such as GitHub or Facebook) into the
 * application's own principal.
 *
 * <p>Delegates to {@link DefaultOAuth2UserService} for the provider's user-info call, then uses
 * {@link OAuth2UserProvisioningService} to look up / provision the local shadow {@link UserEntity}.
 * The returned {@link GreencomUserDetails} implements both {@code UserDetails} and {@code OAuth2User},
 * so the rest of the app treats every login flow uniformly. OIDC providers (e.g. Google) are handled
 * separately by {@link CustomOidcUserService}.
 */
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserProvisioningService provisioningService;

    public CustomOAuth2UserService(OAuth2UserProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        UserEntity userEntity = provisioningService.provisionUser(
                oAuth2User.getAttribute("email"),
                oAuth2User.getAttribute("given_name"),
                oAuth2User.getAttribute("family_name"));

        return new GreencomUserDetails(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getEmail(),
                provisioningService.toAuthorities(userEntity),
                oAuth2User.getAttributes(),
                userEntity.getLastName()
        );
    }
}
