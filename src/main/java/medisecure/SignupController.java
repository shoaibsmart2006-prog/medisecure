package medisecure;

import medisecure.model.Role;
import medisecure.model.User;
import medisecure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SignupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            Model model
    ) {

        name = name.trim();
        email = email.trim().toLowerCase();

        if (name.isEmpty()) {
            model.addAttribute("message", "Name cannot be empty.");
            return "signup";
        }

        if (password.length() < 6) {
            model.addAttribute("message", "Password must be at least 6 characters.");
            return "signup";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("message", "Email already registered.");
            return "signup";
        }

        User user = new User(
                name,
                email,
                passwordEncoder.encode(password),
                Role.valueOf(role)
        );

        userRepository.save(user);

        model.addAttribute("message", "Account created. Please log in.");
        return "login";
    }
}