package kim.birthday;

import kim.birthday.repository.AccountRepository;
import kim.birthday.security.converter.EmailPasswordAuthenticationConverter;
import kim.birthday.security.handler.EmailPasswordAuthenticationFailureHandler;
import kim.birthday.security.handler.EmailPasswordAuthenticationSuccessHandler;
import kim.birthday.security.provider.EmailPasswordAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Profile("test")
@Configuration
@RequiredArgsConstructor
public class TestSecurityConfig{

    private final EmailPasswordAuthenticationConverter converter;
    private final EmailPasswordAuthenticationFailureHandler failureHandler;
    private final EmailPasswordAuthenticationSuccessHandler successHandler;
    private final AccountRepository accountRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(emailPasswordAuthenticationProvider())
                .build();

        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, converter);
        authFilter.setFailureHandler(failureHandler);
        authFilter.setSuccessHandler(successHandler);
        authFilter.setRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login"));

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .authenticationManager(authenticationManager)
                .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider() {
        return new EmailPasswordAuthenticationProvider(accountRepository, passwordEncoder());
    }
}