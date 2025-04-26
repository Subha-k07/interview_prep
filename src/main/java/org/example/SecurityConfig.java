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
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/select-role", "/student-login", "/admin-login", "/student-dashboard", "/admin-dashboard").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin().disable() // Disable default login page for now
                .logout(logout -> logout.permitAll());
        return http.build();
    }
}