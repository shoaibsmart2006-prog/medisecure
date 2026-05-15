package medisecure;

import medisecure.model.Role;
import medisecure.model.User;
import medisecure.repository.UserRepository;
import medisecure.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PatientController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public PatientController(AppointmentService appointmentService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/patient")
    public String showPatientPage(Authentication authentication, Model model) {

        String email = authentication.getName();

        User patient = userRepository.findByEmail(email).orElse(null);

        if (patient == null) {
            model.addAttribute("message", "Patient not found.");
            return "patient";
        }

        model.addAttribute("patientName", patient.getName());
        model.addAttribute("appointments", appointmentService.getPatientAppointments(patient));
        model.addAttribute("doctorCount", userRepository.findByRole(Role.DOCTOR).size());

        return "patient";
    }
}