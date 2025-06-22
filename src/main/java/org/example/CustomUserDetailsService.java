package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user: " + username);
        org.example.User user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        System.out.println("User found: " + user.getUsername() + ", Role: " + user.getRole());
        String role = user.getRole();
        if (role == null || role.isEmpty()) {
            System.out.println("Role is null or empty for user: " + username);
            throw new UsernameNotFoundException("User role is missing for username: " + username);
        }
        // Normalize the role: remove "ROLE_" if present, then convert to uppercase
        String roleName = role.startsWith("ROLE_") ? role.substring(5) : role;
        roleName = roleName.toUpperCase(); // Ensure role is uppercase (e.g., "ADMIN" or "STUDENT")
        System.out.println("Processed role: " + roleName);
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roleName) // Spring Security will add "ROLE_" prefix
                .build();
    }
}