package medisecure;

import medisecure.model.Role;
import medisecure.model.User;
import medisecure.repository.UserRepository;
import medisecure.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class BookingController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public BookingController(AppointmentService appointmentService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/book")
    public String showBookingForm(Model model) {
        List<User> doctors = userRepository.findByRole(Role.DOCTOR);
        model.addAttribute("doctors", doctors);
        return "book";
    }

    @PostMapping("/book")
    public String bookAppointment(
            @RequestParam String doctorName,
            @RequestParam String reason,
            @RequestParam LocalDate date,
            @RequestParam LocalTime time,
            Authentication authentication,
            Model model
    ) {

        List<User> doctors = userRepository.findByRole(Role.DOCTOR);
        model.addAttribute("doctors", doctors);

        doctorName = doctorName.trim();
        reason = reason.trim();

        if (reason.isEmpty()) {
            model.addAttribute("message", "Reason cannot be empty.");
            return "book";
        }

        if (date.isBefore(LocalDate.now())) {
            model.addAttribute("message", "You cannot book an appointment in the past.");
            return "book";
        }

        if (date.equals(LocalDate.now()) && time.isBefore(LocalTime.now())) {
            model.addAttribute("message", "You cannot book a time that has already passed today.");
            return "book";
        }

        String patientEmail = authentication.getName();

        User patient = userRepository.findByEmail(patientEmail).orElse(null);

        if (patient == null) {
            model.addAttribute("message", "Patient not found.");
            return "book";
        }

        User doctor = userRepository.findByName(doctorName).orElse(null);

        if (doctor == null) {
            model.addAttribute("message", "Doctor not found.");
            return "book";
        }

        if (doctor.getRole() != Role.DOCTOR) {
            model.addAttribute("message", "That user is not a doctor.");
            return "book";
        }

        boolean booked = appointmentService.bookAppointment(patient, doctor, date, time, reason);

        if (booked) {
            model.addAttribute("message", "Appointment booked successfully.");
        } else {
            model.addAttribute("message", "That appointment time is unavailable.");
        }

        return "book";
    }
}