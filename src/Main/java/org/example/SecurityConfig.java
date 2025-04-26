package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/select-role").permitAll() // Allow access to welcome page and select-role endpoint
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .formLogin(form -> form
                        .loginPage("/login") // Default login page (you can customize this later)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}