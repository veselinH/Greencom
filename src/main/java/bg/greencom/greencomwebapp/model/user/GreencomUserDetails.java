package bg.greencom.greencomwebapp.model.user;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Unified principal used for both authentication flows:
 * <ul>
 *   <li>Form login — built by {@code GreencomUserDetailsService} (no OAuth2 attributes).</li>
 *   <li>Google OAuth2 login — built by {@code CustomOAuth2UserService} (carries the
 *       provider's user attributes).</li>
 * </ul>
 *
 * <p>By implementing both {@link UserDetails} and {@link OidcUser} (which extends
 * {@code OAuth2User}) the rest of the app (controllers, templates) can treat the logged-in
 * user uniformly regardless of how they signed in.
 *
 * <p>Accessible in Thymeleaf templates via {@code sec:authentication="principal"},
 * e.g. {@code sec:authentication="principal.username"}. Authorities are exposed as
 * {@code ROLE_*} strings so Spring's {@code hasRole('ADMIN')} expressions work directly.</p>
 */
public class GreencomUserDetails implements UserDetails, OidcUser, CredentialsContainer {

    private final String username;
    private String password;
    private final String email;
    private String lastName;
    private final Collection<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    /** Form-login principal — no OAuth2/OIDC provider data. */
    public GreencomUserDetails(String username, String password, String email,
                               Collection<GrantedAuthority> authorities, String lastName) {
        this(username, password, email, lastName, authorities, Collections.emptyMap(), null, null);
    }

    /** Plain OAuth2-login principal — carries the attributes returned by the provider, no ID token. */
    public GreencomUserDetails(String username, String password, String email,
                               Collection<GrantedAuthority> authorities, Map<String, Object> attributes, String lastName) {
        this(username, password, email, lastName, authorities, attributes, null, null);
    }

    /** OIDC-login principal (e.g. Google) — carries provider attributes plus the ID token / user-info. */
    public GreencomUserDetails(String username, String password, String email, String lastName,
                               Collection<GrantedAuthority> authorities, Map<String, Object> attributes,
                               OidcIdToken idToken, OidcUserInfo userInfo) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.authorities = authorities;
        this.attributes = attributes != null ? attributes : Collections.emptyMap();
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Wipes the stored password hash once authentication has completed. Invoked automatically by
     * Spring's {@code ProviderManager} (when {@code eraseCredentials} is enabled, the default), so
     * the hash does not linger in the session-stored principal. The hash is only needed during the
     * credential check, which happens before this is called.
     */
    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    public GreencomUserDetails setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    // --- OAuth2User ---

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        // Stable principal identifier used by Spring's OAuth2 machinery; empty for form login.
        return this.username;
    }

    // --- OidcUser ---

    @Override
    public Map<String, Object> getClaims() {
        return this.idToken != null ? this.idToken.getClaims() : this.attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.idToken;
    }
}