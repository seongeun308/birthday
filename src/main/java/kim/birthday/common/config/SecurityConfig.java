package kim.birthday.common.config;

import kim.birthday.repository.AccountRepository;
import kim.birthday.security.converter.EmailPasswordAuthenticationConverter;
import kim.birthday.security.converter.JwtAuthenticationConverter;
import kim.birthday.security.filter.EmailPasswordAuthenticationFilter;
import kim.birthday.security.filter.JwtAuthenticationFilter;
import kim.birthday.security.handler.EmailPasswordAuthenticationFailureHandler;
import kim.birthday.security.handler.EmailPasswordAuthenticationSuccessHandler;
import kim.birthday.security.handler.JwtAuthenticationFailureHandler;
import kim.birthday.security.handler.JwtAuthenticationSuccessHandler;
import kim.birthday.security.provider.EmailPasswordAuthenticationProvider;
import kim.birthday.security.provider.JwtAuthenticationProvider;
import kim.birthday.service.TokenBlacklistService;
import kim.birthday.util.AuthenticationUserUtils;
import kim.birthday.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

    private final EmailPasswordAuthenticationConverter converter;
    private final EmailPasswordAuthenticationFailureHandler failureHandler;
    private final EmailPasswordAuthenticationSuccessHandler successHandler;
    private final AccountRepository accountRepository;
    private final JwtAuthenticationConverter jwtConverter;
    private final JwtAuthenticationFailureHandler jwtFailureHandler;
    private final JwtAuthenticationSuccessHandler jwtSuccessHandler;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationUserUtils authenticationUserUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider() {
        return new EmailPasswordAuthenticationProvider(accountRepository, passwordEncoder());
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtProvider, tokenBlacklistService, authenticationUserUtils);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(emailPasswordAuthenticationProvider())
                .authenticationProvider(jwtAuthenticationProvider())
                .build();

        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, converter);
        authFilter.setFailureHandler(failureHandler);
        authFilter.setSuccessHandler(successHandler);
        authFilter.setRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login"));


        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(authenticationManager, jwtConverter);
        jwtFilter.setFailureHandler(jwtFailureHandler);
        jwtFilter.setSuccessHandler(jwtSuccessHandler);
        jwtFilter.setRequestMatcher(new NegatedRequestMatcher(new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/login"),
                PathPatternRequestMatcher.withDefaults().matcher("/signup")
        )));

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안함
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager)
                .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, EmailPasswordAuthenticationFilter.class)
                .build();
    }
}
