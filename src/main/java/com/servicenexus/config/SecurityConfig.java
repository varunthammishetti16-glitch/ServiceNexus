
package com.servicenexus.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/customers/register")
            )
            .authorizeHttpRequests(auth -> auth
                // Allow error handling without requiring login.
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                // Customer registration is public.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/customers/register"
                ).permitAll()

                // Public browsing.
                .requestMatchers(
                    HttpMethod.GET,
                    "/",
                    "/services"
                ).permitAll()

                // All other requests require authentication.
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
