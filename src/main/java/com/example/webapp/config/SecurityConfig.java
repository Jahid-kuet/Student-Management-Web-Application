package com.example.webapp.config;

import com.example.webapp.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    /**
     * Security filter chain for REST API endpoints.
     * Uses HTTP Basic authentication and stateless sessions.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                // API endpoints - authenticated users can access, method security handles roles
                .requestMatchers("/api/**").authenticated()
            )
            .httpBasic(basic -> {})
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }

    /**
     * Security filter chain for web MVC endpoints.
     * Uses form login with sessions.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                // Actuator health endpoint (for Docker health checks)
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // Student endpoints - Students can view, but cannot delete their own account
                .requestMatchers("/students/delete/**").hasAnyRole("TEACHER", "AUTHORITY")
                
                // Course management - Only AUTHORITY can add/delete courses
                .requestMatchers("/courses/add", "/courses/store", "/courses/delete/**").hasRole("AUTHORITY")
                .requestMatchers("/courses/edit/**", "/courses/update/**").hasAnyRole("TEACHER", "AUTHORITY")
                .requestMatchers("/courses", "/courses/**").hasAnyRole("STUDENT", "TEACHER", "AUTHORITY")
                
                // Department management - TEACHER and AUTHORITY can manage
                .requestMatchers("/departments/add", "/departments/store", "/departments/delete/**").hasAnyRole("TEACHER", "AUTHORITY")
                .requestMatchers("/departments", "/departments/**").hasAnyRole("STUDENT", "TEACHER", "AUTHORITY")
                
                // User management - Only AUTHORITY can manage users
                .requestMatchers("/users/**").hasRole("AUTHORITY")
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/students", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
