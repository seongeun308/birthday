package kim.birthday.security.filter;

import kim.birthday.security.converter.EmailPasswordAuthenticationConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFilter;

public class EmailPasswordAuthenticationFilter extends AuthenticationFilter {

    public EmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager, new EmailPasswordAuthenticationConverter());
    }
}