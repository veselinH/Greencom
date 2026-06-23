package bg.greencom.greencomwebapp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Redirects to {@code /home} after a successful Google OAuth2 login, mirroring the
 * {@code defaultSuccessUrl("/home", true)} behaviour of the form-login flow.
 *
 * <p>At this point {@code CustomOAuth2UserService} has already provisioned (or looked up)
 * the local {@code UserEntity}, so the authentication principal is a fully populated
 * {@link bg.greencom.greencomwebapp.model.user.GreencomUserDetails}.
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
