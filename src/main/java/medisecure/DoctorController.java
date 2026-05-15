package medisecure;

import medisecure.model.User;
import medisecure.repository.UserRepository;
import medisecure.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DoctorController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public DoctorController(AppointmentService appointmentService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/doctor")
    public String showDoctorPage(Authentication authentication, Model model) {

        String email = authentication.getName();

        User doctor = userRepository.findByEmail(email).orElse(null);

        if (doctor == null) {
            model.addAttribute("message", "Doctor not found.");
            return "doctor";
        }

        model.addAttribute("doctorName", doctor.getName());
        model.addAttribute("appointments", appointmentService.getDoctorAppointments(doctor));

        return "doctor";
    }

    @PostMapping("/doctor/cancel")
    public String cancelAppointment(
            @RequestParam Long appointmentId,
            Authentication authentication,
            Model model
    ) {
        appointmentService.cancelAppointment(appointmentId);

        String email = authentication.getName();
        User doctor = userRepository.findByEmail(email).orElse(null);

        model.addAttribute("doctorName", doctor.getName());
        model.addAttribute("appointments", appointmentService.getDoctorAppointments(doctor));
        model.addAttribute("message", "Appointment cancelled.");

        return "doctor";
    }

    @PostMapping("/doctor/notes")
    public String saveNotes(
            @RequestParam Long appointmentId,
            @RequestParam String doctorNotes,
            Authentication authentication,
            Model model
    ) {
        appointmentService.saveDoctorNotes(appointmentId, doctorNotes);

        String email = authentication.getName();
        User doctor = userRepository.findByEmail(email).orElse(null);

        model.addAttribute("doctorName", doctor.getName());
        model.addAttribute("appointments", appointmentService.getDoctorAppointments(doctor));
        model.addAttribute("message", "Notes saved successfully.");

        return "doctor";
    }
}