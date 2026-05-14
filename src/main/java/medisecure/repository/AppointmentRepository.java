package medisecure.repository;

import medisecure.model.Appointment;
import medisecure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor(User doctor);

    List<Appointment> findByPatient(User patient);

    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(
            User doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );
}