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

public class GreencomUserDetails implements UserDetails, OidcUser, CredentialsContainer {

    private final String username;
    private String password;
    private final String email;
    private final String lastName;
    private final Collection<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    public GreencomUserDetails(String username, String password, String email,
                               Collection<GrantedAuthority> authorities, String lastName) {
        this(username, password, email, lastName, authorities, Collections.emptyMap(), null, null);
    }

    public GreencomUserDetails(String username, String password, String email,
                               Collection<GrantedAuthority> authorities, Map<String, Object> attributes, String lastName) {
        this(username, password, email, lastName, authorities, attributes, null, null);
    }

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

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.username;
    }

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