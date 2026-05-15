package medisecure.service;

import medisecure.model.Appointment;
import medisecure.model.User;
import medisecure.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public boolean bookAppointment(User patient, User doctor, LocalDate date, LocalTime time, String reason) {

        boolean doctorAlreadyBooked = appointmentRepository
                .existsByDoctorAndAppointmentDateAndAppointmentTime(doctor, date, time);

        if (doctorAlreadyBooked) {
            return false;
        }

        boolean patientAlreadyBooked = appointmentRepository
                .existsByPatientAndAppointmentDateAndAppointmentTime(patient, date, time);

        if (patientAlreadyBooked) {
            return false;
        }

        Appointment appointment = new Appointment(date, time, reason, patient, doctor);
        appointmentRepository.save(appointment);

        return true;
    }

    public List<Appointment> getDoctorAppointments(User doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }

    public List<Appointment> getPatientAppointments(User patient) {
        return appointmentRepository.findByPatient(patient);
    }

    public void cancelAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }
}