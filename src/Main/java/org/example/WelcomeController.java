package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String showWelcomePage() {
        return "welcome"; // Maps to welcome.html
    }

    @PostMapping("/select-role")
    public String selectRole(String role) {
        if ("student".equals(role)) {
            return "redirect:/student-dashboard";
        } else if ("admin".equals(role)) {
            return "redirect:/admin-dashboard";
        }
        return "redirect:/";
    }
}
