package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WelcomeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubtopicRepository subtopicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private DownloadRecordRepository downloadRecordRepository;

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
        return "welcome";
    }

    @GetMapping("/student-login")
    public String showStudentLogin(Model model) {
        return "student-login";
    }

    @GetMapping("/admin-login")
    public String showAdminLogin(Model model) {
        return "admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            if ("ROLE_STUDENT".equals(user.getRole())) {
                return "redirect:/student-dashboard";
            } else if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin-dashboard";
            }
        }
        redirectAttributes.addFlashAttribute("error", "Invalid credentials");
        return "redirect:/" + (user != null && "ROLE_ADMIN".equals(user.getRole()) ? "admin-login" : "student-login");
    }

    @PostMapping("/student-signup")
    public String studentSignup(@RequestParam String email, @RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Encoded password: " + encodedPassword);
        user.setPassword(encodedPassword);
        user.setRole("student");
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
        return "redirect:/student-login";
    }

    @PostMapping("/admin-signup")
    public String adminSignup(@RequestParam String email, @RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("admin");
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
        return "redirect:/admin-login";
    }

    @GetMapping("/student-signup")
    public String showStudentSignup() {
        return "student-signup";
    }

    @GetMapping("/admin-signup")
    public String showAdminSignup() {
        return "admin-signup";
    }

    @GetMapping("/student-dashboard")
    public String showStudentDashboard(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        model.addAttribute("subjects", subjectRepository.findAll());
        return "student-dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        model.addAttribute("subjects", subjectRepository.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/admin/add-subject")
    public String showAddSubjectForm() {
        return "add-subject";
    }

    @PostMapping("/admin/add-subject")
    public String addSubject(@RequestParam String subjectName, RedirectAttributes redirectAttributes) {
        Subject subject = new Subject(subjectName);
        subjectRepository.save(subject);
        redirectAttributes.addFlashAttribute("message", "Subject added successfully!");
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/delete-subject")
    public String deleteSubject(@RequestParam String subjectName, RedirectAttributes redirectAttributes) {
        subjectRepository.deleteById(subjectName);
        redirectAttributes.addFlashAttribute("message", "Subject deleted successfully!");
        return "redirect:/admin-dashboard";
    }

    @GetMapping("/admin/manage-subtopics/{subject}")
    public String manageSubtopics(@PathVariable String subject, Model model) {
        model.addAttribute("subject", subject);
        model.addAttribute("subtopics", subtopicRepository.findBySubjectName(subject));
        return "manage-subtopics";
    }

    @GetMapping("/admin/add-subtopic/{subject}")
    public String showAddSubtopicForm(@PathVariable String subject, Model model) {
        model.addAttribute("subject", subject);
        return "add-subtopic";
    }

    @PostMapping("/admin/add-subtopic")
    public String addSubtopic(@RequestParam String subjectName, @RequestParam String subtopicName,
                              @RequestParam("pdfMaterial") MultipartFile pdfMaterial,
                              @RequestParam String referenceLink, RedirectAttributes redirectAttributes) {
        try {
            Subject subject = subjectRepository.findById(subjectName).orElseThrow(() -> new RuntimeException("Subject not found: " + subjectName));
            Subtopic subtopic = new Subtopic(subtopicName, subject, pdfMaterial.getBytes(), referenceLink);
            subtopicRepository.save(subtopic);
            redirectAttributes.addFlashAttribute("message", "Subtopic added successfully!");
        } catch (MaxUploadSizeExceededException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to add subtopic: File size exceeds the maximum limit of 10MB.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to add subtopic: " + e.getMessage());
        }
        return "redirect:/admin/manage-subtopics/" + subjectName;
    }

    @PostMapping("/admin/delete-subtopic")
    public String deleteSubtopic(@RequestParam String subtopicName, @RequestParam String subjectName, RedirectAttributes redirectAttributes) {
        subtopicRepository.deleteById(subtopicName);
        redirectAttributes.addFlashAttribute("message", "Subtopic deleted successfully!");
        return "redirect:/admin/manage-subtopics/" + subjectName;
    }

    @GetMapping("/subtopics/{subject}")
    public String showSubtopics(@PathVariable String subject, Model model) {
        System.out.println("Fetching subtopics for subject: " + subject);
        try {
            if (subtopicRepository == null) {
                throw new RuntimeException("SubtopicRepository is not injected!");
            }
            List<Subtopic> subtopics = subtopicRepository.findBySubjectName(subject);
            System.out.println("Raw subtopics fetched: " + subtopics);
            if (subtopics == null || subtopics.isEmpty()) {
                System.out.println("No subtopics found for subject: " + subject);
                model.addAttribute("message", "No subtopics available for " + subject);
                model.addAttribute("subtopics", new ArrayList<>());
            } else {
                System.out.println("Found subtopics: " + subtopics.size());
                for (Subtopic subtopic : subtopics) {
                    System.out.println("Subtopic: " + subtopic.getName() + ", Subject: " + (subtopic.getSubject() != null ? subtopic.getSubject().getName() : "null"));
                }
                model.addAttribute("subtopics", subtopics);
            }
            model.addAttribute("subject", subject);
        } catch (Exception e) {
            System.out.println("Error fetching subtopics for subject " + subject + ": " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "Error loading subtopics: " + e.getMessage());
            model.addAttribute("subtopics", new ArrayList<>());
        }
        return "subtopics";
    }

    @GetMapping("/content/{subject}/{subtopic}")
    public String showContent(@PathVariable String subject, @PathVariable String subtopic, Model model) {
        Subtopic subtopicEntity = subtopicRepository.findById(subtopic).orElse(null);
        if (subtopicEntity == null) {
            model.addAttribute("message", "Subtopic not found: " + subtopic);
        }
        model.addAttribute("subject", subject);
        model.addAttribute("subtopic", subtopicEntity);
        return "content";
    }

    @GetMapping("/download/{subtopicName}")
    public ResponseEntity<ByteArrayResource> downloadPdf(@PathVariable String subtopicName) {
        Subtopic subtopic = subtopicRepository.findById(subtopicName).orElseThrow(() -> new RuntimeException("Subtopic not found: " + subtopicName));
        ByteArrayResource resource = new ByteArrayResource(subtopic.getPdfMaterial());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        DownloadRecord record = new DownloadRecord(username, subtopicName, LocalDateTime.now());
        downloadRecordRepository.save(record);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + subtopicName + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(subtopic.getPdfMaterial().length)
                .body(resource);
    }

    @GetMapping("/download-history")
    public String showDownloadHistory(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<DownloadRecord> userDownloads = downloadRecordRepository.findByUsername(username);
        model.addAttribute("downloads", userDownloads);
        return "download-history";
    }

    @GetMapping("/suggest-topic")
    public String showSuggestTopicForm(Model model) {
        return "suggest-topic";
    }

    @PostMapping("/suggest-topic")
    public String suggestTopic(@RequestParam String subject, @RequestParam String suggestion, RedirectAttributes redirectAttributes) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Suggestion suggestionEntity = new Suggestion(subject, suggestion, username);
        suggestionRepository.save(suggestionEntity);
        System.out.println("Suggestion saved: Subject=" + subject + ", Suggestion=" + suggestion + ", User=" + username);
        redirectAttributes.addFlashAttribute("message", "Suggestion submitted successfully!");
        return "redirect:/student-dashboard";
    }

    @GetMapping("/admin/view-suggestions")
    public String viewSuggestions(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        model.addAttribute("suggestions", suggestionRepository.findAllByOrderByTimestampDesc());
        return "view-suggestions";
    }

    @GetMapping("/test-password")
    public String testPassword(@RequestParam String username, @RequestParam String rawPassword, Model model) {
        User user = userRepository.findByUsernameAndRole(username, "student");
        if (user == null) {
            model.addAttribute("result", "User not found");
        } else {
            boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
            model.addAttribute("result", "Password match: " + matches);
        }
        return "test-password";
    }
}