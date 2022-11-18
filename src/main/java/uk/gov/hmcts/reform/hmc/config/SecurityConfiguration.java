package uk.gov.hmcts.reform.hmc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] AUTH_ALLOWED_LIST = {
        "/swagger-resources/**",
        "/swagger-ui/**",
        "/webjars/**",
        "/v2/api-docs",
        "/health",
        "/health/liveness",
        "/health/readiness",
        "/info",
        "/favicon.ico",
        "/"
    };

    private final ServiceAuthFilter serviceAuthFilter;

    @Autowired
    public SecurityConfiguration(ServiceAuthFilter serviceAuthFilter) {
        super();
        this.serviceAuthFilter = serviceAuthFilter;
    }

    @Bean
    protected SecurityFilterChain allowedList(HttpSecurity http) throws Exception {
        return http
            .requestMatchers().antMatchers(AUTH_ALLOWED_LIST)
            .and()
            .sessionManagement().sessionCreationPolicy(STATELESS)
            .and()
            .authorizeRequests().anyRequest().permitAll()
            .and()
            .build();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .addFilterBefore(serviceAuthFilter, AbstractPreAuthenticatedProcessingFilter.class)
            .sessionManagement().sessionCreationPolicy(STATELESS)
            .and()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .csrf().disable()
            .build();
    }
}
