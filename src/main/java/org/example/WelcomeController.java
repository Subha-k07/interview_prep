package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String showWelcomePage() {
        return "welcome";
    }

    @PostMapping("/select-role")
    public String selectRole(String role) {
        if ("student".equals(role)) {
            return "student-login";
        } else if ("admin".equals(role)) {
            return "admin-login";
        }
        return "welcome"; // Back to welcome page if role is invalid
    }

    @PostMapping("/student-login")
    public String studentLogin(@RequestParam String username, @RequestParam String password) {
        if (username != null && username.equals("student1") && password.equals("pass123")) {
            return "student-dashboard";
        }
        return "student-login"; // Stay on login page if credentials are invalid
    }

    @PostMapping("/admin-login")
    public String adminLogin(@RequestParam String username, @RequestParam String password) {
        if (username != null && username.equals("admin1") && password.equals("admin123")) {
            return "admin-dashboard";
        }
        return "admin-login"; // Stay on login page if credentials are invalid
    }
}