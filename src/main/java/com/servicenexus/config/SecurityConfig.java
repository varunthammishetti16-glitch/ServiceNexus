package com.servicenexus.config;

import com.servicenexus.repository.WorkerAccountRepository;
import com.servicenexus.service.CustomerUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomerUserDetailsService customerUserDetailsService,
            WorkerAccountRepository workerAccountRepository,
            PasswordEncoder passwordEncoder) {

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("AdminTest@123"))
                .roles("ADMIN")
                .build();

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(username -> {

            String email = username.trim();

            // Admin authentication
            if ("admin".equalsIgnoreCase(email)) {
                return admin;
            }

            // Worker authentication
            return workerAccountRepository
                    .findByEmailIgnoreCase(email)
                    .map(account -> {

                        String status = account.getVerificationStatus();

                        // Only approved workers can log in
                        if (!"APPROVED".equalsIgnoreCase(status)) {
                            throw new DisabledException(
                                    "Your worker account is not approved yet. "
                                    + "Please contact the administrator."
                            );
                        }

                        return User.withUsername(account.getEmail())
                                .password(account.getPasswordHash())
                                .roles("WORKER")
                                .build();
                    })

                    // Customer authentication
                    .orElseGet(() ->
                            customerUserDetailsService
                                    .loadUserByUsername(email)
                    );
        });

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider)
            throws Exception {

        // Login redirection based on user role
        AuthenticationSuccessHandler successHandler =
                (request, response, authentication) -> {

                    boolean isAdmin = authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority()
                                            .equals("ROLE_ADMIN"));

                    boolean isWorker = authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority()
                                            .equals("ROLE_WORKER"));

                    if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");

                    } else if (isWorker) {
                        response.sendRedirect("/worker");

                    } else {
                        response.sendRedirect("/");
                    }
                };

        http
            .authenticationProvider(authenticationProvider)

            // CSRF configuration
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher(
                            "/api/customers/register", "POST"),
                    new AntPathRequestMatcher(
                            "/api/cooperatives", "POST")
                )
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // Public pages and resources
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/services",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/worker/register",
                    "/worker/register/**"
                ).permitAll()

                // Customer registration
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/customers/register"
                ).permitAll()

                // Admin access
                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

                // Customer API access
                .requestMatchers("/api/customers/**")
                .hasRole("CUSTOMER")

                // Worker access
                .requestMatchers("/worker/**")
                .hasRole("WORKER")

                .anyRequest().permitAll()
            )

            // Form login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}