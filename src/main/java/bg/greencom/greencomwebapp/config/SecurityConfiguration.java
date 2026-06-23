package bg.greencom.greencomwebapp.config;

import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.security.OAuth2LoginSuccessHandler;
import bg.greencom.greencomwebapp.service.impl.CustomOAuth2UserService;
import bg.greencom.greencomwebapp.service.impl.CustomOidcUserService;
import bg.greencom.greencomwebapp.service.impl.GreencomUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

/**
 * Spring Security configuration for the Greencom web application.
 *
 * <p>Key design decisions:
 * <ul>
 *   <li>Uses the Spring 6 lambda-style DSL (no deprecated chained {@code .and()} calls).</li>
 *   <li>{@code Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()} keeps password hashes
 *       compatible with accounts created under Boot 2.x / Spring Security 5.x.</li>
 *   <li>{@link DelegatingSecurityContextRepository} combines the request-scoped and
 *       session-scoped strategies, which is required for the manual auto-login performed in
 *       {@code UserController.registerUser} after a successful registration.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfiguration(CustomOAuth2UserService customOAuth2UserService,
                                 CustomOidcUserService customOidcUserService,
                                 OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {

        http
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/users/login", "/users/register", "/about", "/users/login-errors").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/users/login")
                        .usernameParameter(SPRING_SECURITY_FORM_USERNAME_KEY)
                        .passwordParameter(SPRING_SECURITY_FORM_PASSWORD_KEY)
                        .defaultSuccessUrl("/home", true)
                        .failureForwardUrl("/users/login-errors")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)   // plain OAuth2 providers
                                .oidcUserService(customOidcUserService)) // OIDC providers (e.g. Google)
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .sessionManagement(session -> session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new GreencomUserDetailsService(userRepository);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }
}
