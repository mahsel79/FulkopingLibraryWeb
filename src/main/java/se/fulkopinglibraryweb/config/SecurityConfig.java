package se.fulkopinglibraryweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import se.fulkopinglibraryweb.security.ratelimit.RateLimiter;
import se.fulkopinglibraryweb.security.filters.RateLimitingFilter;
import se.fulkopinglibraryweb.security.filters.CsrfRequestMatcher;
import se.fulkopinglibraryweb.security.CustomAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/signup").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .failureHandler(customAuthenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .requireCsrfProtectionMatcher(new CsrfRequestMatcher())
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            )
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                    .preload(true))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; frame-ancestors 'self'; form-action 'self';"))
                .frameOptions(frame -> frame
                    .sameOrigin())
                .addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection", "1; mode=block")))
            .exceptionHandling(handling -> handling
                .accessDeniedPage("/errors/403"));
            
        // Configure rate limiting
        http.addFilterBefore(
            new RateLimitingFilter(rateLimiter(redisTemplate)), 
            UsernamePasswordAuthenticationFilter.class
        );
            
        return http.build();
    }

    @Bean
    public RateLimiter rateLimiter(RedisTemplate<String, String> redisTemplate) {
        return new RateLimiter(redisTemplate, 100, 60, 60); // 100 requests per 60 seconds, 60 second block duration
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
