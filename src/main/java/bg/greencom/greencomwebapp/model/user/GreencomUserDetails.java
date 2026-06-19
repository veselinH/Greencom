package bg.greencom.greencomwebapp.model.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Spring Security {@link UserDetails} implementation that also carries the
 * display-friendly fields (first name, last name, email) needed by the UI.
 *
 * <p>Accessible in Thymeleaf templates via {@code sec:authentication="principal"},
 * e.g. {@code ${#authentication.principal.firstName}}. Authorities are exposed as
 * {@code ROLE_*} strings so Spring's {@code hasRole('ADMIN')} expressions work directly.</p>
 */
public class GreencomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Collection<GrantedAuthority> authorities;

    public GreencomUserDetails(String username, String password, String firstName, String lastName, String email, Collection<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.authorities = authorities;
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
    public String getUsername() {
        return this.username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
