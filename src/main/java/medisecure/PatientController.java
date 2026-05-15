package medisecure;

import medisecure.model.Appointment;
import medisecure.model.Role;
import medisecure.model.User;
import medisecure.repository.UserRepository;
import medisecure.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/patient/cancel")
    public String cancelAppointment(
            @RequestParam Long appointmentId,
            Authentication authentication,
            Model model
    ) {
        String email = authentication.getName();

        User patient = userRepository.findByEmail(email).orElse(null);

        if (patient == null) {
            model.addAttribute("message", "Patient not found.");
            return "patient";
        }

        for (Appointment appointment : appointmentService.getPatientAppointments(patient)) {
            if (appointment.getId().equals(appointmentId)) {
                appointmentService.cancelAppointment(appointmentId);
                model.addAttribute("message", "Appointment cancelled successfully.");
                break;
            }
        }

        model.addAttribute("patientName", patient.getName());
        model.addAttribute("appointments", appointmentService.getPatientAppointments(patient));
        model.addAttribute("doctorCount", userRepository.findByRole(Role.DOCTOR).size());

        return "patient";
    }
}