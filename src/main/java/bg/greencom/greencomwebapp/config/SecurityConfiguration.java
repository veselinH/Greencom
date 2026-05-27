package bg.greencom.greencomwebapp.config;

import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.impl.GreencomUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfiguration {
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new Pbkdf2PasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(
//            HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
//
//        http
//                .authorizeHttpRequests()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                .requestMatchers("/", "/users/login", "/users/register", "/about", "/users/login-errors").permitAll()
//                .anyRequest().authenticated().
//                and().
//                formLogin().
//                loginPage("/users/login").
//                usernameParameter(SPRING_SECURITY_FORM_USERNAME_KEY).
//                passwordParameter(SPRING_SECURITY_FORM_PASSWORD_KEY).
//                defaultSuccessUrl("/home", true)
//                .failureForwardUrl("/users/login-errors")
//                .and()
//                .logout()
//                .logoutUrl("/users/logout")
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .and()
//                .securityContext()
//                .securityContextRepository(securityContextRepository);
//
//
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository) {
//        return new GreencomUserDetailsService(userRepository);
//    }
//
//    @Bean
//    public SecurityContextRepository securityContextRepository() {
//        return new DelegatingSecurityContextRepository(
//                new RequestAttributeSecurityContextRepository(),
//                new HttpSessionSecurityContextRepository()
//        );
//    }
//}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // В новата версия трябва да зададем параметри или да ползваме default
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
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
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
