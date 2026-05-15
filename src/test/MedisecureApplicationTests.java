package medisecure;

import medisecure.model.Role;
import medisecure.model.User;
import medisecure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedisecureApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        userRepository.save(new User(
                "Dr Khan",
                "doctor@test.com",
                passwordEncoder.encode("123456"),
                Role.DOCTOR
        ));

        userRepository.save(new User(
                "Patient User",
                "patient@test.com",
                passwordEncoder.encode("123456"),
                Role.PATIENT
        ));
    }

    @Test
    void contextLoads() {
    }

    @Test
    void signupPageLoads() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk());
    }

    @Test
    void loginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void duplicateSignupBlocked() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("name", "Another Doctor")
                        .param("email", "doctor@test.com")
                        .param("password", "123456")
                        .param("role", "DOCTOR"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email already registered")));
    }

    @Test
    void weakPasswordBlocked() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("name", "Test User")
                        .param("email", "new@test.com")
                        .param("password", "123")
                        .param("role", "PATIENT"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Password must be at least 6 characters")));
    }

    @Test
    void patientCannotAccessDoctorPage() throws Exception {
        mockMvc.perform(get("/doctor")
                        .with(user("patient@test.com").roles("PATIENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorCannotAccessBookingPage() throws Exception {
        mockMvc.perform(get("/book")
                        .with(user("doctor@test.com").roles("DOCTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void bookingPastAppointmentBlocked() throws Exception {
        mockMvc.perform(post("/book")
                        .with(csrf())
                        .with(user("patient@test.com").roles("PATIENT"))
                        .param("doctorName", "Dr Khan")
                        .param("reason", "Checkup")
                        .param("date", LocalDate.now().minusDays(1).toString())
                        .param("time", "10:00"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("You cannot book an appointment in the past")));
    }

    @Test
    void logoutRedirectsHome() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}