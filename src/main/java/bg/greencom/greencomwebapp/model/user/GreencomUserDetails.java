package bg.greencom.greencomwebapp.model.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class GreencomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String firstName;

    private Collection<GrantedAuthority> authorities;

    public GreencomUserDetails(String username, String password, String firstName, Collection<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
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
